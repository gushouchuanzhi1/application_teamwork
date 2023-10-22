package com.hust.homepage.ui

import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.hust.resbase.TimeUtil

@BindingAdapter("time")
fun onTime(view: TextView, created_timestamp: Long) {
    val time = TimeUtil.time(created_timestamp)
    view.text = time
}