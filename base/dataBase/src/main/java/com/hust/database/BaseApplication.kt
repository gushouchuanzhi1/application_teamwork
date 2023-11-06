package com.hust.database

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import com.alibaba.android.arouter.launcher.ARouter

class BaseApplication : Application() {
    private val isDebugARouter = false
    override fun onCreate() {
        super.onCreate()
        context = applicationContext
        if (isDebugARouter) {
            //下面2行必须在ARouter init 之间，否则无效
            //打印日志
            ARouter.openLog()
            //开启调试模式(如果在InstantRun的模式下必须开启，线上必须关闭)
            ARouter.openDebug()
        }
        ARouter.init(this)
    }

    override fun onTerminate() {
        super.onTerminate()
        ARouter.getInstance().destroy()
    }
    companion object {
        @SuppressLint("StaticFieldLeak")
        private lateinit var context: Context

        var currentUseName: String = ""
        var currentUseId: Int = -1
        var currentUsePicPath: String = ""
        var currentUseNickname: String = ""
        var certainFriendPicPath: String = ""
        fun getContext(): Context {
            return context
        }
    }
}