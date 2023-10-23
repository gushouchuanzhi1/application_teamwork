package com.hust.lar.components

import android.Manifest
import android.os.Build
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import com.google.accompanist.permissions.shouldShowRationale
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class PhotoComponent {

    private var openGalleryLauncher: ManagedActivityResultLauncher<Unit?, PictureResult>? = null

    private val scope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    companion object {
        val instance get() = Helper.obj
    }

    private object Helper {
        val obj = PhotoComponent()
    }

    //相册权限flow
    private val checkGalleryImagePermission =
        MutableSharedFlow<Boolean?>(replay = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)

    private fun setCheckGalleryPermissionState(value: Boolean?) {
        scope.launch {
            checkGalleryImagePermission.emit(value)
        }
    }

    /**
     * @param galleryCallback 相册结果回调
     * @param graphCallback 拍照结果回调
     * @param permissionRationale 权限拒绝状态回调
     **/
    @OptIn(ExperimentalPermissionsApi::class, ExperimentalPermissionsApi::class)
    @Composable
    fun Register(
        galleryCallback: (selectResult: PictureResult) -> Unit,
        permissionRationale: ((gallery: Boolean) -> Unit)? = null,
    ) {
        val rememberGalleryCallback = rememberUpdatedState(newValue = galleryCallback)
        openGalleryLauncher = rememberLauncherForActivityResult(contract = SelectPicture()) {
            rememberGalleryCallback.value.invoke(it)
        }

        val readGalleryPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            //13以上的权限申请
            Manifest.permission.READ_MEDIA_IMAGES
        } else {
            Manifest.permission.READ_EXTERNAL_STORAGE
        }

        var permissionGalleryState by rememberSaveable { mutableStateOf(false) }
        val permissionList = arrayListOf(
            readGalleryPermission
        )
        val galleryPermissionState = rememberPermissionState(readGalleryPermission)

        LaunchedEffect(Unit) {
            checkGalleryImagePermission.collectLatest {
                permissionGalleryState = it == true
                if (it == true) {
                    if (galleryPermissionState.status.isGranted) {
                        setCheckGalleryPermissionState(null)
                        openGalleryLauncher?.launch(null)
                    } else if (galleryPermissionState.status.shouldShowRationale) {
                        setCheckGalleryPermissionState(null)
                        permissionRationale?.invoke(true)
                    } else {
                        galleryPermissionState.launchPermissionRequest()
                    }
                }
            }
        }

        LaunchedEffect(galleryPermissionState.status.isGranted) {
            if (galleryPermissionState.status.isGranted && permissionGalleryState) {
                setCheckGalleryPermissionState(null)
                openGalleryLauncher?.launch(null)
            }
        }
    }

    //调用选择图片功能
    fun selectImage() {
        setCheckGalleryPermissionState(true)
    }
}
