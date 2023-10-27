package com.hust.chat

import android.os.Build
import android.os.Bundle
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.hust.chat.databinding.ActivityChatBinding
import com.hust.netbase.ChatUnit

class ChatActivity : AppCompatActivity() {

    private lateinit var binding: ActivityChatBinding
    private val viewModel: ChatActivityViewModel by viewModels()

    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initView()
        initData()
    }

    @RequiresApi(Build.VERSION_CODES.R)
    private fun initView() {
        // 设置状态栏颜色
        val windowController = WindowCompat.getInsetsController(window, window.decorView)
        windowController.isAppearanceLightStatusBars = !resources.configuration.isNightModeActive

        setSupportActionBar(binding.chatToolbar)
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.SpecificChatFragment
            )
        )
        val navController = findNavController(R.id.nav_host_fragment_content_chat)
        setupActionBarWithNavController(navController, appBarConfiguration)
    }

    private fun initData() {
        val bundle = intent.extras
        bundle?.let {
            viewModel.chatUnit = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                it.getSerializable("chatUnit", ChatUnit::class.java)
            } else {
                it.getSerializable("chatUnit") as ChatUnit
            }
        }
        binding.friendNickname.text = viewModel.chatUnit?.nickname
    }
}