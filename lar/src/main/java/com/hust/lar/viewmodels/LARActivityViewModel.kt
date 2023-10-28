package com.hust.lar.viewmodels

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModel
import com.hust.database.AppRoomDataBase
import com.hust.database.BaseApplication
import com.hust.database.MMKVUtil
import com.hust.database.tables.User
import com.hust.resbase.Constant
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import kotlin.random.Random

class LARActivityViewModel : ViewModel() {
    var tip by mutableStateOf<String?>(null)

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
        return if (checkFormat(username, 1) and checkFormat(password, 2)) {
            try {
                val deffer = scope.async {
                    appRoomDataBase.userDao().queryByLoginIn(username, password)
                }
                val user = deffer.await()
                if (user == null) {
                    tip = "该用户不存在！"
                } else {
                    MMKVUtil.getMMKV(BaseApplication.getContext())
                        .put(Constant.CURRENT_USER_ID, user.id)
                    MMKVUtil.getMMKV(BaseApplication.getContext())
                        .put(Constant.CURRENT_USER_NICKNAME, user.nickname)
                    MMKVUtil.getMMKV(BaseApplication.getContext())
                        .put(Constant.CURRENT_USER_PICPATH, user.profilePicPath)
                }
                user != null
            } catch (e: Exception) {
                e.printStackTrace()
                tip = "数据库查询出错！"
                false
            }
        } else {
            tip = "邮箱或者密码格式错误！"
            false
        }
    }

    suspend fun signUp(username: String, password: String, nickname: String, uri: Uri): Boolean {
        return if (checkFormat(username, 1) and checkFormat(password, 2)) {
            try {
                val deffer = scope.async {
                    if(appRoomDataBase.userDao().queryByName(username) == null) {
                        val user = User(
                            userName = username,
                            password = password,
                            createdAt = System.currentTimeMillis(),
                            nickname = nickname,
                            profilePicPath = uri.toString()
                        )
                        user.id = Random(System.currentTimeMillis()).nextInt(Int.MAX_VALUE)
                        appRoomDataBase.runInTransaction {
                            appRoomDataBase.userDao().insert(user)
                        }
                        appRoomDataBase.userDao().queryByLoginIn(username, password) != null
                    }else {
                        false
                    }
                }
                val isExist = deffer.await()
                tip = if (isExist) "注册成功" else "注册失败"
                isExist
            } catch (e: Exception) {
                e.printStackTrace()
                tip = "数据库操作出错！"
                false
            }
        } else {
            tip = "邮箱或者密码格式错误！"
            false
        }
    }

    fun createCopyAndReturnRealPath(context: Context, uri: Uri): Uri? {
        val contentResolver = context.contentResolver ?: return null
        val file = File(context.filesDir, System.currentTimeMillis().toString())
        try {
            val inputStream = contentResolver.openInputStream(uri) ?: return null
            file.parentFile?.mkdirs()

            file.createNewFile()
            val outputStream: OutputStream = FileOutputStream(file)
            val buf = ByteArray(1024)
            var len: Int
            while (inputStream.read(buf).also { len = it } > 0) outputStream.write(buf, 0, len)
            outputStream.close()
            inputStream.close()
            return FileProvider.getUriForFile(context, context.packageName + ".fileprovider", file)
        } catch (e: IOException) {
            e.printStackTrace()
            return null
        }
    }
}