package com.example.newsapp.data.APIClient


import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient  {
    val apiService: APIService
    private var instance: RetrofitClient? = null
    private val BASE_URL = "https://content.guardianapis.com"
    init {
        val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()
        apiService = retrofit.create(APIService::class.java)
    }
}
