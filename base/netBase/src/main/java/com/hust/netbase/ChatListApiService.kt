package com.hust.netbase

import com.hust.resbase.NetworkConstant
import retrofit2.Response
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ChatListApiService : MyChatApi.BaseApiService {
    @GET("chat/chatlist")
    suspend fun getChatList(
        @Query("id") id: Int,
        @Query("timestamp") timestamp: String,
        @Query("offset") offset: Int = 0,
        @Query("mode") mode: String = NetworkConstant.SortMode.LATEST_REPLY,
        @Query("limit") limit: Int = NetworkConstant.CONSTANT_STANDARD_LOAD_SIZE
    ): Response<List<ChatUnit>>

    /** 删除某一个记录 **/
    @DELETE("chat/{useId}")
    suspend fun deleteTheChat(
        @Path("useId") useId: Int
    ): Response<Unit>
}