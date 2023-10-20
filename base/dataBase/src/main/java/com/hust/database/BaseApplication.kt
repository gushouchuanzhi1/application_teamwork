package com.hust.database

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context

class BaseApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        context = applicationContext
    }
    companion object {
        @SuppressLint("StaticFieldLeak")
        private lateinit var context: Context

        fun getContext(): Context {
            return context
        }
    }
}