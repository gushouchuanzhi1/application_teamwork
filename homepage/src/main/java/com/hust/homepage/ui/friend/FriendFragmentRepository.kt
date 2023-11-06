package com.hust.homepage.ui.friend

import com.hust.database.AppRoomDataBase
import com.hust.database.BaseApplication
import com.hust.database.tables.RecommendUserSong
import com.hust.netbase.UserLike
import com.hust.resbase.ApiResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import org.json.JSONObject
import retrofit2.Response

class FriendFragmentRepository {
    private val dispatcher = Dispatchers.IO
    private val dataBase = AppRoomDataBase.get()

    fun likeTheSong(songId: String): Flow<ApiResult> = flow {
        emit(ApiResult.Loading())
        val isLike = dataBase.recommendDao().isLikeTheSong(BaseApplication.currentUseId, songId) != null
        if(isLike) {
            dataBase.recommendDao().cancelLike(BaseApplication.currentUseId, songId)
        }else {
            dataBase.recommendDao().insert(
                RecommendUserSong(
                    userId = BaseApplication.currentUseId,
                    songId = songId,
                    createAt = System.currentTimeMillis()
                ))
        }
        emit(ApiResult.Success(data = isLike))
    }.flowOn(dispatcher).catch {
        it.printStackTrace()
        emit(ApiResult.Error(code = -1, errorMessage = "数据库操作出错"))
    }

    fun getRec(): Flow<ApiResult> = flow<ApiResult> {
        emit(ApiResult.Loading())
        // 存储歌曲信息
        val songMap: HashMap<String, Array<String>> = HashMap()
        // 解析出歌曲id、名称和风格信息，存放到songMap中
        dataBase.songDao().getAllSongs().forEach { song ->
            songMap[song.songId] = arrayOf(song.songName, song.songGenres)
        }
        // 获取用户喜欢的歌曲id
        val loveList = dataBase.recommendDao().getRecommendByUser(BaseApplication.currentUseId)

        val candidateMap = HashMap<String, Int>() // 存储候选推荐歌曲及其匹配的风格数
        for (loveSongId in loveList) {
            val loveSongInfo = songMap[loveSongId.songId]
                ?: continue  // 忽略没有在songlist.csv中出现的歌曲
            val genres =
                loveSongInfo[1].split("\\|".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
            songMap.forEach { (songId, songInfo) ->
                if (loveSongId.songId == songId) {
                    return@forEach
                }
                var matchCount = 0
                for (genre in genres) {
                    if (songInfo[1].contains(genre)) {
                        matchCount++
                    }
                }
                if (matchCount > 0) {
                    candidateMap[songId] = matchCount // 满足条件的歌曲加入候选推荐列表
                }
            }
        }
        val recommendedSongs = ArrayList<UserLike>()
        while (recommendedSongs.size < FriendFragmentViewModel.RECOMMEND_NUM && candidateMap.isNotEmpty()) {
            // 按照匹配风格数从高到低排序，选择前n首歌曲作为推荐结果
            var maxMatchSongId: String? = null
            var maxMatchCount = 0
            candidateMap.forEach { (songId, matchCount) ->
                if (maxMatchSongId == null || matchCount > maxMatchCount) {
                    maxMatchSongId = songId
                    maxMatchCount = matchCount
                }
            }
            recommendedSongs.add(
                UserLike(
                    RecommendUserSong(userId = -1, songId = maxMatchSongId ?: "", createAt =  0L),
                    "每日推荐",
                    "android.resource://com.hust.mychat/drawable/ic_cloudmusic",
                    dataBase.songDao().getSongById(maxMatchSongId?:"").songName,
                    dataBase.recommendDao().isLikeTheSong(BaseApplication.currentUseId, maxMatchSongId?:"") != null
                )
            )
            candidateMap.remove(maxMatchSongId)
        }
        emit(ApiResult.Success(data = recommendedSongs))
    }.flowOn(dispatcher).catch {
        it.printStackTrace()
        emit(ApiResult.Error(code = -1, errorMessage = it.message))
    }

    fun getFriend(): Flow<ApiResult> = flow {
        emit(ApiResult.Loading())
        val friends = dataBase.userToUserDao().queryFriends(BaseApplication.currentUseId)
        val list = mutableListOf<UserLike>()
        dataBase.runInTransaction {
            friends?.let {
                it.forEach { friend ->
                    val likeList = dataBase.recommendDao().getRecommendByUser(friend.friendId)
                    likeList.forEach { like ->
                        val userLike = UserLike(
                            like,
                            friend.friendNickname,
                            friend.friendProfilePicPath,
                            dataBase.songDao().getSongById(like.songId).songName,
                            dataBase.recommendDao().isLikeTheSong(BaseApplication.currentUseId, like.songId) != null
                        )
                        list.add(userLike)
                    }
                }
            }
        }
        emit(ApiResult.Success(data = list))
    }.flowOn(dispatcher).catch {
        it.printStackTrace()
        emit(ApiResult.Error(code = -1, errorMessage = it.message))
    }

    fun getLike(): Flow<ApiResult> = flow {
        emit(ApiResult.Loading())
        val list = mutableListOf<UserLike>()
        dataBase.runInTransaction {
            dataBase.recommendDao().getRecommendByUser(BaseApplication.currentUseId).forEach { rec ->
                val userLike = UserLike(
                    rec,
                    BaseApplication.currentUseNickname,
                    BaseApplication.currentUsePicPath,
                    dataBase.songDao().getSongById(rec.songId).songName,
                    true
                )
                list.add(userLike)
            }
        }
        emit(ApiResult.Success(data = list))
    }.flowOn(dispatcher).catch {
        it.printStackTrace()
        emit(ApiResult.Error(code = -1, errorMessage = it.message))
    }


    private suspend inline fun <T> checkResponse(
        response: Response<T>?,
        flow: FlowCollector<ApiResult>
    ) {
        if (response?.isSuccessful == true) {
            flow.emit(ApiResult.Success(data = response.body()))
        } else {
            val json = response?.errorBody()?.string()
            val jsonObject = json?.let { JSONObject(it) }
            val returnCondition = jsonObject?.getString("errorMsg")
            val errorCode = jsonObject?.getString("errorCode")
            flow.emit(
                ApiResult.Error(
                    code = errorCode?.toInt() ?: response?.code() ?: 0,
                    errorMessage = returnCondition
                )
            )
            response?.errorBody()?.close()
        }
    }
}