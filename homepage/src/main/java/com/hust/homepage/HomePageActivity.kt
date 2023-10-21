package com.hust.homepage

import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.annotation.RequiresApi
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.hust.homepage.databinding.ActivityHomePageBinding

class HomePageActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomePageBinding

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomePageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initView()
        backTwiceExit()
    }

    private fun backTwiceExit() {
        var lastPressedTime = 0L
        onBackPressedDispatcher.addCallback(object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if(lastPressedTime + 2000L > System.currentTimeMillis()) {
                    finish()
                }else {
                    Toast.makeText(this@HomePageActivity, "请再按一次退出", Toast.LENGTH_SHORT).show()
                }
                lastPressedTime = System.currentTimeMillis()
            }
        })
    }

    @RequiresApi(Build.VERSION_CODES.R)
    private fun initView() {
        setSupportActionBar(binding.homeScreenToolbar)

        // 设置状态栏颜色
        val windowController = WindowCompat.getInsetsController(window, window.decorView)
        windowController.isAppearanceLightStatusBars = !resources.configuration.isNightModeActive

        val navView: BottomNavigationView = binding.navView

        val navController = findNavController(R.id.nav_host_fragment_activity_home_page)

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
    }
}