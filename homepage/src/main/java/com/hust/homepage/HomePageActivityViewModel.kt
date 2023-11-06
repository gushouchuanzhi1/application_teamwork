package com.hust.homepage

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hust.database.AppRoomDataBase
import com.hust.database.BaseApplication
import com.hust.database.tables.ChatRecord
import com.hust.database.tables.User
import com.hust.database.tables.UserToUser
import com.hust.netbase.PlayList
import com.hust.netbase.Song
import com.hust.netbase.WebPageRequest
import com.hust.resbase.OnFileReadCallback
import com.hust.resbase.OnFunctionCallBack
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.InputStream
import java.security.SecureRandom

class HomePageActivityViewModel : ViewModel() {
    private val appRoomDataBase: AppRoomDataBase = AppRoomDataBase.get()
    private val scope = CoroutineScope(Dispatchers.IO)
    var tip = MutableLiveData<String?>()
    val isRefresh = MutableLiveData<Boolean>()
    val isInsert = MutableLiveData<Boolean>()

    fun searchAndAddFriend(friendName: String, onCallBack: OnFunctionCallBack) {
        var user: User? = null
        scope.launch {
            try {
                user = appRoomDataBase.userDao().queryByName(friendName)
            }catch (e: Exception) {
                e.printStackTrace()
                tip.value = e.message
            }
            user?.let {
                if(appRoomDataBase.userToUserDao().hasFriend(BaseApplication.currentUseId, it.nickname) == null) {
                    val chatId = generateChatId()
                    val self = UserToUser(
                        selfId = BaseApplication.currentUseId,
                        friendNickname = it.nickname,
                        friendProfilePicPath = it.profilePicPath,
                        chatId = chatId
                    )
                    val chatRecord = ChatRecord(
                        chatId = self.chatId,
                        content = "你好呀",
                        createAt = System.currentTimeMillis(),
                        ownerId = it.id
                    )
                    try {
                        appRoomDataBase.userToUserDao().insert(self)
                        if(it.id != BaseApplication.currentUseId) {
                            val friend = UserToUser(
                                selfId = it.id,
                                friendNickname = BaseApplication.currentUseNickname,
                                friendProfilePicPath = BaseApplication.currentUsePicPath,
                                chatId = chatId
                            )
                            appRoomDataBase.userToUserDao().insert(friend)
                        }

                        appRoomDataBase.chatRecordDao().insert(chatRecord)
                    }catch (e: Exception) {
                        e.printStackTrace()
                        tip.value = e.message
                    }
                    withContext(Dispatchers.Main) {
                        onCallBack.onSuccess()
                    }
                }
            }
        }
    }

    fun initPlayListData(input: InputStream) {
        WebPageRequest.getPlayList(input, object : OnFileReadCallback {
            override fun onSuccess(list: List<*>) {
                viewModelScope.launch {
                    isInsert.value = true
                }
                appRoomDataBase.runInTransaction {
                    list.forEach { playList ->
                        appRoomDataBase.playListDao().insert((playList as PlayList).asTablePlayList())
                    }
                }
                viewModelScope.launch {
                    tip.value = "PlayList插入完成！"
                    isInsert.value = false
                }
            }

            override fun onFailure(msg: CharSequence) {
                viewModelScope.launch {
                    tip.value = msg.toString()
                }
            }
        })
    }

    fun initSongListData(input: InputStream) {
        WebPageRequest.getSongList(input, object : OnFileReadCallback {
            override fun onSuccess(list: List<*>) {
                viewModelScope.launch {
                    isInsert.value = true
                }
                appRoomDataBase.runInTransaction {
                    list.forEach { song ->
                        appRoomDataBase.songDao().insert((song as Song).asTableSong())
                    }
                }
                viewModelScope.launch {
                    tip.value = "SongList插入完成！"
                    isInsert.value = false
                }
            }

            override fun onFailure(msg: CharSequence) {
                viewModelScope.launch {
                    tip.value = msg.toString()
                }
            }

        })
    }

    fun doneShowingTip() {
        tip.value = null
    }

    private fun generateChatId(): String {
        val charPool: List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9')
        val random = SecureRandom()

        return (1..13)
            .map { charPool[random.nextInt(charPool.size)] }
            .joinToString("")
    }
}