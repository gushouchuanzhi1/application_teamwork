package com.hust.resbase

interface OnFunctionCallBack {
    fun onSuccess() {}
    fun onFailure() {}
}

interface OnFileReadCallback {
    fun onSuccess(list: List<*>)

    fun onFailure(msg: CharSequence)
}