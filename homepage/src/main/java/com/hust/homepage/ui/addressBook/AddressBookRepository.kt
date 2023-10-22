package com.hust.homepage.ui.addressBook

import com.hust.database.AppRoomDataBase
import com.hust.database.BaseApplication
import com.hust.database.tables.ChatRecord
import com.hust.netbase.ChatUnit
import com.hust.resbase.ApiResult
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import org.json.JSONObject
import retrofit2.Response

class AddressBookRepository {
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
    private val appRoomDataBase: AppRoomDataBase = AppRoomDataBase.get()

    fun getLocalAddressList(): Flow<ApiResult> = flow {
        emit(ApiResult.Loading())
        val friends = appRoomDataBase.userToUserDao().queryFriends(BaseApplication.currentUseId)
        val addressList = mutableListOf<ChatUnit>()
        friends?.forEach { friend ->
            addressList.add(ChatUnit(-1, friend.friendProfilePicPath, friend.friendNickname, ChatRecord()))
        }
        emit(ApiResult.Success(data = addressList))
    }.flowOn(dispatcher).catch {
        it.printStackTrace()
        emit(ApiResult.Error(code = -1, errorMessage = it.message))
    }

    private suspend inline fun <T> checkResponse(
        response: Response<T>?,
        flow: FlowCollector<ApiResult>
    ) {
        if (response?.isSuccessful == true) {
            flow.emit(ApiResult.Success(data = response.body()))
        } else {
            val json = response?.errorBody()?.string()
            val jsonObject = json?.let { JSONObject(it) }
            val returnCondition = jsonObject?.getString("errorMsg")
            val errorCode = jsonObject?.getString("errorCode")
            flow.emit(
                ApiResult.Error(
                    code = errorCode?.toInt() ?: response?.code() ?: 0,
                    errorMessage = returnCondition
                )
            )
            response?.errorBody()?.close()
        }
    }
}