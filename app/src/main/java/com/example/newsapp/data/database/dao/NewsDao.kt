package com.example.newsapp.data.database.dao

import androidx.room.*
import com.example.newsapp.data.model.NewsItem
import io.reactivex.Flowable
import io.reactivex.Single

@Dao
interface NewsDao {

    @get:Query("SELECT * FROM newsItem WHERE favorite = '1'")
    val allFavorite: Flowable<List<NewsItem>>

    @get:Query("SELECT * FROM newsItem WHERE saved = '1'")
    val allSavedNews: Single<List<NewsItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(newsItem: NewsItem)

    @Query("DELETE FROM newsItem WHERE id =:id")
    fun delete(id: String)

    @Query("SELECT * FROM newsItem WHERE id = :id")
    fun getNewsItemById(id: String): Single<NewsItem>

    @Update
    fun update(item: NewsItem)


}