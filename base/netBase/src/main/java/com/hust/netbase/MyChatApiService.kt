package com.hust.netbase

import android.content.Context
import com.hust.database.MMKVUtil
import com.hust.resbase.Constant
import com.hust.resbase.DateUtil
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit

// 假设现在有这样的服务器可以通过下面的URL访问资源
private const val BASE_URL = "https://myChat.com/api/"

object MyChatApi {
    @JvmStatic
    fun <T : BaseApiService> init(
        context: Context,
        `class`: Class<T>,
        baseUrl: String = BASE_URL
    ): T {
        val token = MMKVUtil.getMMKV(context).getString(Constant.LOGIN_TOKEN) ?: ""

        val httpLoggingInterceptor =
            HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.HEADERS)

        val requestInterceptor = Interceptor { chain ->
            chain.request().newBuilder()
                .addHeader("os", "android")
                .addHeader("dateTime", DateUtil.dateTime)
                .addHeader("Accept-Language", "zh-CN")
                .build()
                .let { chain.proceed(it) }
        }

        val tokenInterceptor = Interceptor { chain ->
            chain.request().newBuilder()
                .addHeader(
                    "Authorization",
                    token
                )
                .build()
                .let { chain.proceed(it) }
        }

        val okhttpClient = OkHttpClient.Builder()
            .connectTimeout(3, TimeUnit.SECONDS)
            .addInterceptor(requestInterceptor)
            .addInterceptor(tokenInterceptor)
            .addInterceptor(httpLoggingInterceptor)
            .build()

        val moshi =
            Moshi.Builder()
                .add(KotlinJsonAdapterFactory())
                .build()

        val retrofit = Retrofit.Builder()
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .client(okhttpClient)
            .baseUrl(baseUrl)
            .build()
        return retrofit.create(`class`)
    }

    interface BaseApiService {}
}