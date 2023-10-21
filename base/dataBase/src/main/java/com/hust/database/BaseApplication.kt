package com.hust.database

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import com.hust.resbase.Constant

class BaseApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        context = applicationContext
    }
    companion object {
        @SuppressLint("StaticFieldLeak")
        private lateinit var context: Context

        var currentUseId: Int = -1
        fun getContext(): Context {
            return context
        }
    }
}