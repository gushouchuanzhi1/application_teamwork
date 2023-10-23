package com.hust.homepage.ui.home

import androidx.lifecycle.MutableLiveData
import com.hust.database.AppRoomDataBase
import com.hust.database.BaseApplication
import com.hust.database.tables.ChatRecord
import com.hust.netbase.ChatListApiService
import com.hust.netbase.ChatUnit
import com.hust.resbase.ApiResult
import com.hust.resbase.DateUtil
import com.hust.resbase.NetworkConstant.CONSTANT_STANDARD_LOAD_SIZE
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onEach
import org.json.JSONObject
import retrofit2.Response

class HomeRepository(
    private val apiService: ChatListApiService
) {
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
    private val appRoomDataBase: AppRoomDataBase = AppRoomDataBase.get()
    private var offset = 0
    var tip = MutableLiveData("")

    fun getChatList(sortMode: String): Flow<ApiResult> = flow {
        emit(ApiResult.Loading())
        val resp = apiService.getChatList(
            id = BaseApplication.currentUseId,
            timestamp = refreshTimestamp(),
            mode = sortMode
        )
        checkResponse(resp, this)
    }.flowOn(dispatcher).onEach {
        offset = 0
    }.catch {
        it.printStackTrace()
        tip.value = it.message
    }

    fun loadMoreChat(): Flow<ApiResult> = flow {
        emit(ApiResult.Loading())
        offset += CONSTANT_STANDARD_LOAD_SIZE
        val resp = apiService.getChatList(
            id = BaseApplication.currentUseId,
            timestamp = refreshTimestamp(),
            offset = offset
        )
        checkResponse(resp, this)
    }.flowOn(dispatcher).catch { e ->
        tip.value = e.message
        e.printStackTrace()
    }

    fun deleteChat(chatUnit: ChatUnit): Flow<ApiResult> = flow {
        emit(ApiResult.Loading())
        val resp = apiService.deleteTheChat(
            chatUnit.use_id
        )
        checkResponse(resp, this)
    }.flowOn(dispatcher)
        .catch {
            it.printStackTrace()
            tip.value = it.message
        }

    fun getLocalChatList(sortMode: String): Flow<ApiResult> = flow {
        emit(ApiResult.Loading())
        val friends = appRoomDataBase.userToUserDao().queryFriends(BaseApplication.currentUseId)
        val chatList = mutableListOf<ChatUnit>()
        friends?.let {
            it.forEach { friend ->
                val oneRecord = appRoomDataBase.chatRecordDao().queryOneRecord(friend.chatId)
                chatList.add(ChatUnit(-1, friend.friendProfilePicPath, friend.friendNickname, oneRecord))
            }
        }
        emit(ApiResult.Success(data = chatList))
    }.flowOn(dispatcher).onEach {
        offset = 0
    }.catch {
        it.printStackTrace()
        emit(ApiResult.Error(code = -1, errorMessage = it.message))
    }

    private fun refreshTimestamp(): String = DateUtil.dateTime

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