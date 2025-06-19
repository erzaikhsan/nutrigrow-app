package com.project.labs.nutrigrow.data.remote.retrofit

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ApiConfig {
    companion object {
        fun getApiService(): ApiService {
            val authInterceptor = Interceptor {
                val req = it.request()
                val requestHeaders = req.newBuilder().build()
                it.proceed(requestHeaders)
            }
            val loggingInterceptor =
                HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
            val client = OkHttpClient.Builder()
                .addInterceptor(authInterceptor)
                .addInterceptor(loggingInterceptor)
                .build()
            val retrofit = Retrofit.Builder()
//                .baseUrl("https://tn2fx920-3000.asse.devtunnels.ms/api/v1/")
                .baseUrl("http://192.168.0.110:3000/api/v1/")
//                .baseUrl("http://192.168.157.221:3000/api/v1/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build()
            return retrofit.create(ApiService::class.java)
        }
    }
}