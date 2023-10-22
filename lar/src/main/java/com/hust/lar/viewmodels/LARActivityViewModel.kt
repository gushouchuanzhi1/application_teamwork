package com.hust.lar.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hust.database.AppRoomDataBase
import com.hust.database.BaseApplication
import com.hust.database.MMKVUtil
import com.hust.database.tables.User
import com.hust.resbase.Constant
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class LARActivityViewModel : ViewModel() {
    var tip by mutableStateOf("")
    private val appRoomDataBase: AppRoomDataBase = AppRoomDataBase.get()
    private val scope = CoroutineScope(Dispatchers.IO)

    fun checkFormat(value: String, type: Int): Boolean {
        // type为1时检查userName格式，为2时检查password格式
        return when (type) {
            1 -> {
                val emailRegex = "^[A-Za-z0-9]+@[a-zA-Z0-9]+(.[a-zA-Z0-9]+)+$"
                value.matches(Regex(emailRegex))
            }
            2 -> {
                val validPasswordRegex = "^[a-zA-Z0-9]+$"
                value.matches(Regex(validPasswordRegex))
            }
            else -> {
                false
            }
        }
    }

    suspend fun loginIn(username: String, password: String): Boolean {
        return if(checkFormat(username, 1) and checkFormat(password, 2)) {
            try {
                val deffer = scope.async {
                    appRoomDataBase.userDao().queryByLoginIn(username, password)
                }
                val user = deffer.await()
                if(user == null) {tip = "该用户不存在！"} else {
                    MMKVUtil.getMMKV(BaseApplication.getContext()).put(Constant.CURRENT_USER_ID, user.id)
                }
                user != null
            }catch (e:Exception) {
                e.printStackTrace()
                tip = "数据库查询出错！"
                false
            }
        }else {
            tip = "邮箱或者密码格式错误！"
            false
        }
    }

    suspend fun signUp(username: String, password: String, nickname: String): Boolean {
        return if(checkFormat(username, 1) and checkFormat(password, 2)) {
            try {
                val deffer = scope.async {
                    val user = User()
                    user.userName = username
                    user.password = password
                    user.createdAt = System.currentTimeMillis()
                    user.nickname = nickname
                    appRoomDataBase.userDao().insert(user)
                    appRoomDataBase.userDao().queryByLoginIn(username, password) != null
                }
                val isExist = deffer.await()
                if(!isExist) {tip = "注册失败"}
                isExist
            }catch (e:Exception) {
                e.printStackTrace()
                tip = "数据库操作出错！"
                false
            }
        }else {
            tip = "邮箱或者密码格式错误！"
            false
        }
    }
}