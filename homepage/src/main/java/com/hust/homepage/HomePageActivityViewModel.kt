package com.hust.homepage

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hust.database.AppRoomDataBase
import com.hust.database.BaseApplication
import com.hust.database.tables.ChatRecord
import com.hust.database.tables.User
import com.hust.database.tables.UserToUser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import java.security.SecureRandom

class HomePageActivityViewModel : ViewModel() {
    private val appRoomDataBase: AppRoomDataBase = AppRoomDataBase.get()
    private val scope = CoroutineScope(Dispatchers.IO)
    var tip = MutableLiveData("")

    suspend fun searchAndAddFriend(friendName: String): Boolean {
        var user: User? = null
        val deffer = scope.async {
            try {
                user = appRoomDataBase.userDao().queryByName(friendName)
            }catch (e: Exception) {
                e.printStackTrace()
                tip.value = e.message
                return@async false
            }
            user?.let {
                val userToUser = UserToUser()
                userToUser.selfId = BaseApplication.currentUseId
                userToUser.friendNickname = it.nickname
                userToUser.friendProfilePicPath = it.profilePicPath
                userToUser.chatId = generateChatId()
                val chatRecord = ChatRecord()
                chatRecord.chatId = userToUser.chatId
                chatRecord.content = "你好呀"
                chatRecord.createAt = System.currentTimeMillis()
                chatRecord.ownerId = it.id
                try {
                    appRoomDataBase.userToUserDao().insert(userToUser)
                    appRoomDataBase.chatRecordDao().insert(chatRecord)
                    return@async true
                }catch (e: Exception) {
                    e.printStackTrace()
                    tip.value = e.message
                    return@async false
                }
            }
            false
        }
        return deffer.await()
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