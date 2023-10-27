package com.hust.chat

import android.text.Editable
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hust.database.AppRoomDataBase
import com.hust.database.tables.ChatRecord
import com.hust.netbase.ChatUnit
import com.hust.resbase.ApiResult
import com.hust.resbase.ApiStatus
import com.hust.resbase.PlaceholderType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SpecificFragmentViewModel : ViewModel() {
    private val repository = SpecificFragmentRepository()

    private val _chatList = MutableStateFlow<List<ChatRecord>>(listOf())

    val chatList = _chatList.asStateFlow()
    var tip = MutableLiveData<String?>()

    fun getChatList(chatId: String) {
        viewModelScope.launch {
            repository.getChatList(chatId).collect {
                when(it) {
                    is ApiResult.Success<*> -> {
                        _chatList.emit(it.data as List<ChatRecord>)
                    }

                    is ApiResult.Error -> {
                        tip.value = it.code.toString() + it.errorMessage
                    }

                    else -> {}
                }
            }
        }
    }

    fun sendAMessage(message: Editable, chatId: String) {
        viewModelScope.launch {
            repository.sendAMessage(message.toString(), chatId).collect {
                when(it) {
                    is ApiResult.Success<*> -> {
                        tip.value = "发送成功！"
                        _chatList.emit(_chatList.value.toMutableList().apply { add(it.data as ChatRecord) })
                        message.clear()
                    }

                    is ApiResult.Error -> {
                        tip.value = it.code.toString() + it.errorMessage
                    }

                    else -> {}
                }
            }
        }
    }

    fun doneTipShow() {
        tip.value = null
    }
}