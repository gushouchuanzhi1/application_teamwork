package com.hust.mychat

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.hust.database.AppRoomDataBase
import com.hust.database.BaseApplication
import com.hust.database.MMKVUtil
import com.hust.database.tables.RecommendUserSong
import com.hust.database.tables.User
import com.hust.resbase.ArouterConfig
import com.hust.resbase.Constant
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Timer
import java.util.TimerTask
import kotlin.random.Random

@Route(path = ArouterConfig.ACTIVITY_START)
class StartActivity : AppCompatActivity() {
    private val userList = listOf(
        User(
            id = Random(System.currentTimeMillis()).nextInt(Int.MAX_VALUE),
            "John@qq.com",
            "123456",
            System.currentTimeMillis(),
            "Jhon",
            "android.resource://com.hust.mychat/drawable/ic_default_pro_pic"
        ),
        User(
            id = Random(System.currentTimeMillis()).nextInt(Int.MAX_VALUE),
            "Amy@qq.com",
            "123456",
            System.currentTimeMillis(),
            "Amy",
            "android.resource://com.hust.mychat/drawable/ic_default_pro_pic"
        )
    )
    private val likeList = listOf(
        RecommendUserSong(
            userId = userList[0].id,
            songId = "1405430727",
            createAt = System.currentTimeMillis()
        ),
        RecommendUserSong(
            userId = userList[0].id,
            songId = "1405888465",
            createAt = System.currentTimeMillis()
        ),
        RecommendUserSong(
            userId = userList[1].id,
            songId = "82203",
            createAt = System.currentTimeMillis()
        ),
        RecommendUserSong(
            userId = userList[1].id,
            songId = "2037937776",
            createAt = System.currentTimeMillis()
        )
    )
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)
        ARouter.getInstance().inject(this)
        // 判断是否已有登录信息
        val mmkv = MMKVUtil.getMMKV(this)
        val isLogin = mmkv.getBoolean(Constant.IS_LOGIN)
        val `class` = if(isLogin) ArouterConfig.ACTIVITY_HOME else ArouterConfig.ACTIVITY_LAR
        if(!mmkv.getBoolean(Constant.IS_FIRST_LOGIN)) {
            CoroutineScope(Dispatchers.IO).launch {
                val appRoomDataBase = AppRoomDataBase.get()
                appRoomDataBase.runInTransaction {
                    userList.forEach {
                        appRoomDataBase.userDao().insert(it)
                    }
                    likeList.forEach {
                        appRoomDataBase.recommendDao().insert(it)
                    }
                }
            }
        }

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