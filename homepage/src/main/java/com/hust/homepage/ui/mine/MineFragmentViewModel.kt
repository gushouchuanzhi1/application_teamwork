package com.hust.homepage.ui.mine

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.datamatrix.encoder.SymbolShapeHint
import com.google.zxing.qrcode.QRCodeWriter
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel
import com.hust.database.AppRoomDataBase
import com.hust.database.BaseApplication
import com.hust.database.tables.User
import com.hust.netbase.FindUnit
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.util.Hashtable

class MineFragmentViewModel : ViewModel() {
    private val appRoomDataBase: AppRoomDataBase = AppRoomDataBase.get()
    private val scope = CoroutineScope(Dispatchers.IO)
    var tip = MutableLiveData<String?>()

    private val _mineList = MutableStateFlow(listOf(
        FindUnit("服务", "android.resource://com.hust.mychat/drawable/ic_service"),
        FindUnit("收藏", "android.resource://com.hust.mychat/drawable/ic_star"),
        FindUnit("朋友圈", "android.resource://com.hust.mychat/drawable/ic_find_friend"),
        FindUnit("卡包", "android.resource://com.hust.mychat/drawable/ic_card_packet"),
        FindUnit("分享", "android.resource://com.hust.mychat/drawable/ic_share"),
        FindUnit("退出", "android.resource://com.hust.mychat/drawable/ic_exit")
    ))

    val mineList = _mineList.asStateFlow()

    fun doneShowingTip() {
        tip.value = null
    }

    fun getPersonalProfile(onCallback: suspend (self: User?) -> Unit) {
        scope.launch {
            val self = appRoomDataBase.userDao().queryById(BaseApplication.currentUseId)
            onCallback(self)
        }
    }

    fun generate(view: View): Bitmap? {
        val bitmap: Bitmap?
        try {
            bitmap = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
        }catch (e: Exception) {
            e.printStackTrace()
            tip.value = "图片生成失败！"
            return null
        }
        val canvas = Canvas(bitmap)
        view.draw(canvas)
        return bitmap
    }

    suspend fun save(context: Context, photo: Bitmap, fileName: String) {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val imageSaveFilePath = Environment.DIRECTORY_DCIM + File.separator + "hustHole"
            val file = File(imageSaveFilePath)
            if(!file.exists()) {
                file.mkdirs()
            }
            val contentValues = ContentValues()
            contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
            contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            contentValues.put(MediaStore.MediaColumns.DATE_TAKEN, System.currentTimeMillis())
            contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, imageSaveFilePath)
            var uri: Uri? = null
            var fos: OutputStream? = null
            val localContentResolver = context.contentResolver
            try {
                withContext(Dispatchers.IO) {
                    uri = localContentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
                    fos = uri?.let { localContentResolver.openOutputStream(it) }

                    photo.compress(Bitmap.CompressFormat.JPEG, 100, fos)
                    fos?.flush()
                    fos?.close()
                }
                tip.value = "保存成功！"
            }catch (e: IOException) {
                e.printStackTrace()
                uri?.let {
                    localContentResolver.delete(it, null, null)
                }
                tip.value = "文件保存失败！"
            }finally {
                photo.recycle()
                try {
                    withContext(Dispatchers.IO) {
                        fos?.close()
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }else {
            val path = Environment.getExternalStorageDirectory().absolutePath + File.separator + "hustHole"
            // 创建文件夹
            val file = File(path, fileName)
            try {
                withContext(Dispatchers.IO) {
                    val fos = FileOutputStream(file)
                    // 通过io流的方式来压缩保存图片
                    photo.compress(Bitmap.CompressFormat.JPEG, 80, fos)
                    fos.flush()
                    fos.close()
                }
                // 保存图片后发送广播通知更新数据库
                val uri = Uri.fromFile(file)
                context.sendBroadcast(Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri))
                tip.value = "保存成功！"
            } catch (e: IOException) {
                e.printStackTrace()
                tip.value = "文件保存失败！"
            }
        }
    }

    fun generateQRCode(url: String): Bitmap? {
        var bitmap: Bitmap? = null
        try {
            val hints = Hashtable<EncodeHintType, Any>()
            hints[EncodeHintType.CHARACTER_SET] = "utf-8"
            hints[EncodeHintType.ERROR_CORRECTION] = ErrorCorrectionLevel.H
            hints[EncodeHintType.DATA_MATRIX_SHAPE] = SymbolShapeHint.FORCE_SQUARE
            val matrix = QRCodeWriter().encode(url, BarcodeFormat.QR_CODE, 1080, 1080, hints)
            val width = matrix.width
            val height = matrix.height
            val pixels = IntArray(width * height)
            for (y in 0 until height) {
                for (x in 0 until width) {
                    if (matrix.get(x, y)) {
                        pixels[y * width + x] = Color.BLACK
                    } else {
                        pixels[y * width + x] = Color.WHITE
                    }
                }
            }
            bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            bitmap.setPixels(pixels, 0, width, 0, 0, width, height)
            return bitmap
        } catch (e: Exception) {
            e.printStackTrace()
            bitmap?.recycle()
            return null
        }
    }
}