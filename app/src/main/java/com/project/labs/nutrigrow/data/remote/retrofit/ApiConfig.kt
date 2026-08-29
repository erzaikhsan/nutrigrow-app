package com.project.labs.nutrigrow.data.remote.retrofit

import com.project.labs.nutrigrow.BuildConfig
import com.project.labs.nutrigrow.data.local.preference.UserPreference
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class ApiConfig {
    companion object {
        private const val BASE_URL = "https://nutrigrow-rest-api.vercel.app/api/v1/"

        private const val CONNECT_TIMEOUT_SECONDS = 30L
        private const val READ_TIMEOUT_SECONDS = 60L
        private const val WRITE_TIMEOUT_SECONDS = 30L

        @Volatile
        private var INSTANCE: ApiService? = null

        fun getApiService(userPreference: UserPreference): ApiService {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: create(userPreference).also { INSTANCE = it }
            }
        }

        private fun create(userPreference: UserPreference): ApiService {
            val client = OkHttpClient.Builder()
                .addInterceptor(authInterceptor(userPreference))
                .addInterceptor(loggingInterceptor())
                .connectTimeout(CONNECT_TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .readTimeout(READ_TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .writeTimeout(WRITE_TIMEOUT_SECONDS, TimeUnit.SECONDS)
                .build()

            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build()
                .create(ApiService::class.java)
        }

        private fun authInterceptor(userPreference: UserPreference) = Interceptor { chain ->
            val request = chain.request()

            if (request.header("Authorization") != null) {
                return@Interceptor chain.proceed(request)
            }

            val token = runBlocking { userPreference.getAuth().first().token }

            val authorized = if (token.isBlank()) {
                request
            } else {
                request.newBuilder().header("Authorization", token).build()
            }

            chain.proceed(authorized)
        }

        private fun loggingInterceptor() = HttpLoggingInterceptor().setLevel(
            if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor.Level.BODY
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
        )
    }
}
