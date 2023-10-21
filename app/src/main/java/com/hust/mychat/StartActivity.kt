package com.hust.mychat

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.hust.database.BaseApplication
import com.hust.database.MMKVUtil
import com.hust.homepage.HomePageActivity
import com.hust.lar.LARActivity
import com.hust.resbase.Constant
import java.util.Timer
import java.util.TimerTask

class StartActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)

        // 判断是否已有登录信息
        val mmkv = MMKVUtil.getMMKV(this)
        val isLogin = mmkv.getBoolean(Constant.IS_LOGIN)
        val `class` = if(isLogin) HomePageActivity::class.java else LARActivity::class.java
        BaseApplication.currentUseId = mmkv.getInt(Constant.CURRENT_USER_ID) ?: -1

        // 设定定时跳转的任务
        val timer = Timer()
        val timerTask = object : TimerTask() {
            override fun run() {
                val intent = Intent(this@StartActivity,
                    `class`)
                startActivity(intent)
                finish()
            }
        }
        timer.schedule(timerTask, 3000)
    }
}