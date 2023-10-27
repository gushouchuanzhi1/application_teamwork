package com.hust.homepage.ui.find

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.hust.netbase.FindUnit
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class FindFragmentViewModel : ViewModel() {
    val tip = MutableLiveData<String>()
    private val _findList = MutableStateFlow(listOf(
        FindUnit("朋友圈", "android.resource://com.hust.mychat/drawable/ic_find_friend"),
        FindUnit("视频号", "android.resource://com.hust.mychat/drawable/ic_find_video"),
        FindUnit("直播", "android.resource://com.hust.mychat/drawable/ic_find_live"),
        FindUnit("扫一扫", "android.resource://com.hust.mychat/drawable/ic_find_scan"),
        FindUnit("摇一摇", "android.resource://com.hust.mychat/drawable/ic_find_shake"),
        FindUnit("看一看", "android.resource://com.hust.mychat/drawable/ic_find_see"),
        FindUnit("搜一搜", "android.resource://com.hust.mychat/drawable/ic_find_search"),
        FindUnit("附近", "android.resource://com.hust.mychat/drawable/ic_find_nearby"),
        FindUnit("购物", "android.resource://com.hust.mychat/drawable/ic_find_buy"),
        FindUnit("游戏", "android.resource://com.hust.mychat/drawable/ic_find_game"),
        FindUnit("小程序", "android.resource://com.hust.mychat/drawable/ic_find_smallapp"),
    ))

    val findList = _findList.asStateFlow()

    fun doneShowingTip() {
        tip.value = null
    }
}