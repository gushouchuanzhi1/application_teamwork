package com.hust.chat

import com.hust.database.AppRoomDataBase
import com.hust.database.BaseApplication
import com.hust.database.tables.ChatRecord
import com.hust.resbase.ApiResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import org.json.JSONObject
import retrofit2.Response

class SpecificFragmentRepository {
    private val dispatcher = Dispatchers.IO
    private val appRoomDataBase: AppRoomDataBase = AppRoomDataBase.get()

    suspend fun getChatList(chatId: String): Flow<ApiResult> = flow{
        emit(ApiResult.Loading())
        val chatList = appRoomDataBase.chatRecordDao().queryRecord(chatId)
        emit(ApiResult.Success(data = chatList))
    }.flowOn(dispatcher).catch {
        emit(ApiResult.Error(code = -1, errorMessage = it.message))
    }

    suspend fun sendAMessage(message: String, chatId: String): Flow<ApiResult> = flow {
        emit(ApiResult.Loading())
        val chatRecord = ChatRecord(
            ownerId = BaseApplication.currentUseId,
            chatId = chatId,
            content = message,
            createAt = System.currentTimeMillis()
        )
        appRoomDataBase.chatRecordDao().insert(chatRecord)
        emit(ApiResult.Success(data = chatRecord))
    }.flowOn(dispatcher).catch {
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