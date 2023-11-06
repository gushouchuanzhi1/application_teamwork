package com.hust.homepage.ui.friend

import android.widget.ImageView
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hust.database.AppRoomDataBase
import com.hust.database.BaseApplication
import com.hust.database.tables.RecommendUserSong
import com.hust.homepage.R
import com.hust.netbase.UserLike
import com.hust.resbase.ApiResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FriendFragmentViewModel : ViewModel() {
    private val dataBase = AppRoomDataBase.get()
    private val _list = MutableStateFlow<ArrayList<UserLike>>(arrayListOf())
    private val repository = FriendFragmentRepository()

    val list = _list.asStateFlow()
    val tip = MutableLiveData<String?>()

    fun doneShowingTip() {
        tip.value = null
    }

    // 根据用户喜欢的歌曲推荐一些相似风格的歌曲
    fun getRec() {
        viewModelScope.launch {
            viewModelScope.launch {
                repository.getRec().collect {
                    when(it) {
                        is ApiResult.Success<*> -> {
                            _list.emit(it.data as ArrayList<UserLike>)
                        }
                        is ApiResult.Error -> {
                            tip.value = it.code.toString() + it.errorMessage
                        }
                        else -> {}
                    }
                }
            }
        }
    }

    fun getFriend() {
        viewModelScope.launch {
            repository.getFriend().collect {
                when(it) {
                    is ApiResult.Success<*> -> {
                        _list.emit(it.data as ArrayList<UserLike>)
                    }
                    is ApiResult.Error -> {
                        tip.value = it.code.toString() + it.errorMessage
                    }
                    else -> {}
                }
            }
        }
    }

    fun getLike() {
        viewModelScope.launch {
            repository.getLike().collect {
                when(it) {
                    is ApiResult.Success<*> -> {
                        _list.emit(it.data as ArrayList<UserLike>)
                    }
                    is ApiResult.Error -> {
                        tip.value = it.code.toString() + it.errorMessage
                    }
                    else -> {}
                }
            }
        }
    }

    fun likeTheSong(view: ImageView, songId: String) {
        viewModelScope.launch {
            repository.likeTheSong(songId).collect {
                when(it) {
                    is ApiResult.Success<*> -> {
                        if(it.data as Boolean) {
                            view.setImageResource(com.hust.resbase.R.drawable.ic_no_like)
                        }else {
                            view.setImageResource(com.hust.resbase.R.drawable.ic_like)
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

    companion object {
        const val RECOMMEND_NUM = 50 // 推荐歌曲数
    }
}