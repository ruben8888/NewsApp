package com.example.newsapp.data.APIClient

import com.example.newsapp.data.model.News

import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface APIService {
    @GET("/search")
    fun getAllNews(@Header("api-key") apiKey: String, @Query("page") currentPage: Int, @Query("show-fields") vararg fields: String): Single<News>

    @GET("/search")
    fun getNewsAfterLastDate(@Header("api-key") apiKey: String, @Query("from-date") lastDate: String, @Query("show-fields") vararg fields: String): Single<News>
}
