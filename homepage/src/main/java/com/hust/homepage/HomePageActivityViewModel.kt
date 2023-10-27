package com.hust.homepage

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.hust.database.AppRoomDataBase
import com.hust.database.BaseApplication
import com.hust.database.tables.ChatRecord
import com.hust.database.tables.User
import com.hust.database.tables.UserToUser
import com.hust.resbase.OnFunctionCallBack
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.security.SecureRandom

class HomePageActivityViewModel : ViewModel() {
    private val appRoomDataBase: AppRoomDataBase = AppRoomDataBase.get()
    private val scope = CoroutineScope(Dispatchers.IO)
    var tip = MutableLiveData<String?>()
    val isRefresh = MutableLiveData<Boolean>()

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