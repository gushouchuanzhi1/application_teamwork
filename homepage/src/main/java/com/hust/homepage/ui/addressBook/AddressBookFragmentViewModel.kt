package com.hust.homepage.ui.addressBook

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hust.database.tables.ChatRecord
import com.hust.netbase.ChatUnit
import com.hust.resbase.ApiResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AddressBookFragmentViewModel : ViewModel() {
    private val repository = AddressBookRepository()
    private val _addressBookList = MutableStateFlow(listOf(
        ChatUnit(-1, "", "新的朋友", ChatRecord()),
        ChatUnit(-1, "", "仅聊天的朋友", ChatRecord()),
        ChatUnit(-1, "", "群聊", ChatRecord()),
        ChatUnit(-1, "", "标签", ChatRecord()),
        ChatUnit(-1, "", "公众号", ChatRecord()),
        ChatUnit(-1, "", "企业微信联系人", ChatRecord()),
        ChatUnit(-1, "", "华中科技大学", ChatRecord())
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