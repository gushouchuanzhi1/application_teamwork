package com.hust.netbase

import com.hust.resbase.NetworkConstant
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface WebApiService : MyChatApi.BaseApiService {
    @GET("discover/playlist")
    suspend fun getPlaylist(
        @Query("cat") cat: String = "",
        @Query("offset") offset: Int = 0,
        @Query("limit") limit: Int = NetworkConstant.CONSTANT_STANDARD_LOAD_SIZE,
        @Query("order") order: String = "hot"
    ): Response<Unit>
}