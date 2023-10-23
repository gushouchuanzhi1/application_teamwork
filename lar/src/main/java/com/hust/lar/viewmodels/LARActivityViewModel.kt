package com.hust.lar.viewmodels

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hust.database.AppRoomDataBase
import com.hust.database.BaseApplication
import com.hust.database.MMKVUtil
import com.hust.database.tables.User
import com.hust.lar.LARActivity
import com.hust.resbase.Constant
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream

class LARActivityViewModel : ViewModel() {
    var tip by mutableStateOf("")
    var picPath by mutableStateOf<File?>(null)

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

    fun chooseAvatarFromGallery(context: Context) {
        try {
            val intent = Intent()
            intent.putExtra(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            intent.type = "image/*"
            (context as LARActivity).requestDataLauncher.launch(intent)
        }catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun createCopyAndReturnRealPath(context: Context, uri: Uri){
        val contentResolver = context.contentResolver ?: return
        val filePath: String = (context.dataDir.absolutePath + File.separator
                + System.currentTimeMillis())
        val file = File(filePath)
        try {
            val inputStream = contentResolver.openInputStream(uri) ?: return
            viewModelScope.launch {
                file.parentFile?.mkdirs()
                withContext(Dispatchers.IO) {
                    file.createNewFile()
                    val outputStream: OutputStream = FileOutputStream(file)
                    val buf = ByteArray(1024)
                    var len: Int
                    while (inputStream.read(buf).also { len = it } > 0) outputStream.write(buf, 0, len)
                    outputStream.close()
                    inputStream.close()
                }
            }
        } catch (e: IOException) {
            e.printStackTrace()
            return
        }
        picPath = file
    }
}