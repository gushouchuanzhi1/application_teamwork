package com.hust.homepage

import android.os.Build
import android.os.Bundle
import android.view.Gravity
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Button
import android.widget.EditText
import android.widget.PopupWindow
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.asynclayoutinflater.view.AsyncLayoutInflater
import androidx.core.view.WindowCompat
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.alibaba.android.arouter.facade.annotation.Route
import com.alibaba.android.arouter.launcher.ARouter
import com.hust.database.BaseApplication
import com.hust.database.MMKVUtil
import com.hust.homepage.databinding.ActivityHomePageBinding
import com.hust.netbase.WebApiService
import com.hust.netbase.WebPageRequest
import com.hust.resbase.ArouterConfig
import com.hust.resbase.Constant
import com.hust.resbase.OnFunctionCallBack
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Route(path = ArouterConfig.ACTIVITY_HOME)
class HomePageActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomePageBinding
    private val viewModel: HomePageActivityViewModel by viewModels()
    private val lifecycleOwner: LifecycleOwner = this
    private lateinit var navController: NavController

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomePageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initData()
        initView()
        backTwiceExit()
    }

    private fun backTwiceExit() {
        var lastPressedTime = 0L
        onBackPressedDispatcher.addCallback(object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (lastPressedTime + 2000L > System.currentTimeMillis()) {
                    finish()
                } else {
                    Toast.makeText(this@HomePageActivity, "请再按一次退出", Toast.LENGTH_SHORT)
                        .show()
                }
                lastPressedTime = System.currentTimeMillis()
            }
        })
    }

    private fun initData() {
        ARouter.getInstance().inject(this)
        BaseApplication.currentUseId = MMKVUtil.getMMKV(this).getInt(Constant.CURRENT_USER_ID) ?: -1
        BaseApplication.currentUseNickname = MMKVUtil.getMMKV(this).getString(Constant.CURRENT_USER_NICKNAME) ?: ""
        BaseApplication.currentUsePicPath = MMKVUtil.getMMKV(this).getString(Constant.CURRENT_USER_PICPATH) ?: ""

        viewModel.initPlayListData(resources.assets.open("playList.csv"))
        viewModel.initSongListData(resources.assets.open("songList.csv"))
    }

    @RequiresApi(Build.VERSION_CODES.R)
    private fun initView() {
        setSupportActionBar(binding.homeScreenToolbar)

        // 设置状态栏颜色
        val windowController = WindowCompat.getInsetsController(window, window.decorView)
        windowController.isAppearanceLightStatusBars = !resources.configuration.isNightModeActive

        val navView: BottomNavigationView = binding.navView

        navController = findNavController(R.id.nav_host_fragment_activity_home_page)

        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home,
                R.id.navigation_address_book,
                R.id.navigation_find,
                R.id.navigation_mine
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        viewModel.tip.observe(lifecycleOwner) {
            it?.let {
                Toast.makeText(this@HomePageActivity, it, Toast.LENGTH_SHORT)
                    .show()
                viewModel.doneShowingTip()
            }
        }

        binding.ivAdd.setOnClickListener {
            createPopUpWindow()
        }
    }

    private fun createPopUpWindow() {
        AsyncLayoutInflater(this).inflate(
            R.layout.add_friend_popupwindow, null
        ) { view, _, _ ->
            val ppwSearch = PopupWindow(view)
            val window = this.window
            with(ppwSearch) {
                isOutsideTouchable = true  //点击卡片外部退出
                isFocusable = true     //按返回键允许退出
                width = ViewGroup.LayoutParams.MATCH_PARENT
                height = ViewGroup.LayoutParams.WRAP_CONTENT
                animationStyle = com.hust.resbase.R.style.PageAnim
            }
            //减弱背景亮度
            window.attributes.alpha = 0.6f
            window.setWindowAnimations(com.hust.resbase.R.style.darkScreenAnim)
            ppwSearch.showAtLocation(
                window.decorView, Gravity.CENTER,
                0, 0
            )

            val etUserId = view.findViewById<EditText>(R.id.et_user_id)
            val btSearch = view.findViewById<Button>(R.id.bt_search_user)
            var isEnabled = false
            etUserId.addTextChangedListener {
                isEnabled = it?.isNotEmpty() ?: false
                btSearch.isEnabled = isEnabled
            }
            ppwSearch.setOnDismissListener {
                cancelDarkBackGround()
            }

            etUserId.setOnEditorActionListener { v, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    val text = v.text.toString()
                    viewModel.searchAndAddFriend(text, object : OnFunctionCallBack {
                        override fun onSuccess() {
                            ppwSearch.dismiss()
                            viewModel.tip.value = "添加成功！"
                            viewModel.isRefresh.value = true
                        }
                    })
                }
                false
            }
            btSearch.setOnClickListener {
                val text = etUserId.text.toString()
                etUserId.text.clear()
                viewModel.searchAndAddFriend(text, object : OnFunctionCallBack {
                    override fun onSuccess() {
                        ppwSearch.dismiss()
                        viewModel.tip.value = "添加成功！"
                        viewModel.isRefresh.value = true
                    }
                })
            }
        }
    }

    private fun cancelDarkBackGround() {
        val lp = this.window.attributes
        lp.alpha = 1f // 0.0~1.0
        this.window.attributes = lp
    }   //取消暗背景
}