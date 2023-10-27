package com.hust.homepage.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hust.database.BaseApplication
import com.hust.netbase.ChatListApiService
import com.hust.netbase.ChatUnit
import com.hust.netbase.MyChatApi
import com.hust.resbase.ApiResult
import com.hust.resbase.ApiStatus
import com.hust.resbase.NetworkConstant
import com.hust.resbase.PlaceholderType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {
    var retrofitService: ChatListApiService = MyChatApi.init(BaseApplication.getContext(), ChatListApiService::class.java)
    private val repository = HomeRepository(retrofitService)
    val tip: MutableLiveData<String?> by lazy { repository.tip }

    private val _chatList = MutableStateFlow<List<ChatUnit>>(listOf())
    private val _loadingState = MutableStateFlow(ApiStatus.SUCCESSFUL)
    private val _sortMode = MutableLiveData(NetworkConstant.SortMode.LATEST)
    private val _showingPlaceholder = MutableLiveData<PlaceholderType>()

    val chatList = _chatList.asStateFlow()
    val loadingState = _loadingState.asStateFlow()
    val sortMode: LiveData<String> = _sortMode
    val showingPlaceholder: LiveData<PlaceholderType> = _showingPlaceholder

    fun getChatList(sortMode: String = _sortMode.value!!) {
        viewModelScope.launch {
            _loadingState.emit(ApiStatus.LOADING)
            repository.getChatList(sortMode).collect {
                when (it) {
                    is ApiResult.Success<*> -> {
                        _loadingState.emit(ApiStatus.SUCCESSFUL)
                        _sortMode.value = sortMode
                        _chatList.emit(it.data as List<ChatUnit>)
                        if (_chatList.value.isEmpty())
                            _showingPlaceholder.value = PlaceholderType.PLACEHOLDER_NO_CONTENT
                    }

                    is ApiResult.Error -> {
                        _loadingState.emit(ApiStatus.ERROR)
                        tip.value = it.code.toString() + it.errorMessage
                        _showingPlaceholder.value = PlaceholderType.PLACEHOLDER_NETWORK_ERROR
                    }

                    else -> {}
                }
            }
        }
    }

    fun loadMoreChat() {
        viewModelScope.launch {
            _loadingState.emit(ApiStatus.LOADING)
            repository.loadMoreChat().collect {
                when (it) {
                    is ApiResult.Success<*> -> {
                        _loadingState.emit(ApiStatus.SUCCESSFUL)
                        _chatList.emit(_chatList.value.toMutableList().apply { addAll(it as List<ChatUnit>) })
                        if (_chatList.value.isEmpty())
                            _showingPlaceholder.value = PlaceholderType.PLACEHOLDER_NO_CONTENT
                    }

                    is ApiResult.Error -> {
                        _loadingState.emit(ApiStatus.ERROR)
                        tip.value = it.code.toString() + it.errorMessage
                        _showingPlaceholder.value = PlaceholderType.PLACEHOLDER_NETWORK_ERROR
                    }

                    else -> {}
                }
            }
        }
    }

    fun deleteChat(chatUnit: ChatUnit) {
        viewModelScope.launch {
            repository.deleteChat(chatUnit)
                .collect {
                    when (it) {
                        is ApiResult.Success<*> -> {
                            tip.value = "删除成功"
                            getChatList()
                        }

                        is ApiResult.Error -> {
                            tip.value = it.code.toString() + it.errorMessage
                        }

                        else -> {}
                    }
                }
        }
    }

    fun getLocalChatList(sortMode: String = _sortMode.value!!) {
        viewModelScope.launch {
            _loadingState.emit(ApiStatus.LOADING)
            repository.getLocalChatList(sortMode).collect {
                when (it) {
                    is ApiResult.Success<*> -> {
                        _loadingState.emit(ApiStatus.SUCCESSFUL)
                        _sortMode.value = sortMode
                        _chatList.emit(it.data as List<ChatUnit>)
                        if (_chatList.value.isEmpty()) {
                            _showingPlaceholder.value = PlaceholderType.PLACEHOLDER_NO_CONTENT
                        }
                    }

                    is ApiResult.Error -> {
                        _loadingState.emit(ApiStatus.ERROR)
                        tip.value = it.code.toString() + it.errorMessage
                        _showingPlaceholder.value = PlaceholderType.PLACEHOLDER_NETWORK_ERROR
                    }

                    else -> {}
                }
            }
        }
    }

    fun doneShowingTip() {
        repository.tip.value = null
    }
}