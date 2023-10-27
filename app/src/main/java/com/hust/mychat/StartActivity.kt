package com.hust.mychat

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.hust.database.MMKVUtil
import com.hust.resbase.ArouterConfig
import com.hust.resbase.Constant
import java.util.Timer
import java.util.TimerTask

@Route(path = ArouterConfig.ACTIVITY_START)
class StartActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)
        ARouter.getInstance().inject(this)
        // 判断是否已有登录信息
        val mmkv = MMKVUtil.getMMKV(this)
        val isLogin = mmkv.getBoolean(Constant.IS_LOGIN)
        val `class` = if(isLogin) ArouterConfig.ACTIVITY_HOME else ArouterConfig.ACTIVITY_LAR

        // 设定定时跳转的任务
        val timer = Timer()
        val timerTask = object : TimerTask() {
            override fun run() {
                ARouter.getInstance().build(`class`).navigation(this@StartActivity)
                finish()
            }
        }
        timer.schedule(timerTask, 3000)
    }
}