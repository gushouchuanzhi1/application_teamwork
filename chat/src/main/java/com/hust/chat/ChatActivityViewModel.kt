package com.hust.chat

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.hust.netbase.ChatUnit

class ChatActivityViewModel : ViewModel() {
    val chatUnit = MutableLiveData<ChatUnit?>()
}