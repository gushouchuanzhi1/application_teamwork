package com.hust.homepage.ui.addressBook

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hust.database.tables.ChatRecord
import com.hust.netbase.ChatUnit
import com.hust.resbase.ApiResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AddressBookFragmentViewModel : ViewModel() {
    private val repository = AddressBookRepository()
    private val _addressBookList = MutableStateFlow(listOf(
        ChatUnit(0, "android.resource://com.hust.mychat/drawable/ic_new_friend", "新的朋友", ChatRecord(0, "", "", 0L)),
        ChatUnit(1, "android.resource://com.hust.mychat/drawable/ic_chat_only", "仅聊天的朋友", ChatRecord(0, "", "", 0L)),
        ChatUnit(2, "android.resource://com.hust.mychat/drawable/ic_chat_group", "群聊", ChatRecord(0, "", "", 0L)),
        ChatUnit(3, "android.resource://com.hust.mychat/drawable/ic_chat_sign", "标签", ChatRecord(0, "", "", 0L)),
        ChatUnit(4, "android.resource://com.hust.mychat/drawable/ic_chat_public", "公众号", ChatRecord(0, "", "", 0L)),
        ChatUnit(5, "android.resource://com.hust.mychat/drawable/ic_mine", "企业微信联系人", ChatRecord(0, "", "", 0L)),
        ChatUnit(6, "android.resource://com.hust.mychat/drawable/ic_mine", "华中科技大学", ChatRecord(0, "", "", 0L))
    ))

    val addressBookList = _addressBookList.asStateFlow()
    var newGetList = listOf<ChatUnit>()
    val tip = MutableLiveData("")

    fun getLocalAddressList() {
        viewModelScope.launch {
            repository.getLocalAddressList().collect {
                when (it) {
                    is ApiResult.Success<*> -> {
                        if(!newGetList.containsAll(it.data as List<ChatUnit>)) {
                            _addressBookList.emit(_addressBookList.value.toMutableList().apply { addAll(it.data as List<ChatUnit>) })
                            newGetList = it.data as List<ChatUnit>
                        }
                    }

                    is ApiResult.Error -> {
                        tip.value = it.code.toString() + it.errorMessage
                    }

                    else -> {}
                }
            }
        }
    }

    fun doneShowingTip() {
        tip.value = null
    }
}