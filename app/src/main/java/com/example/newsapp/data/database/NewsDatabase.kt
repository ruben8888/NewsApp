package com.example.newsapp.data.database


import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

import com.example.newsapp.data.database.dao.NewsDao
import com.example.newsapp.data.model.Fields
import com.example.newsapp.data.model.NewsItem
import com.example.newsapp.utils.NetworkUtils

@Database(entities = [NewsItem::class, Fields::class], version = 1, exportSchema = false)
@TypeConverters(NetworkUtils.Converters::class)
abstract class NewsDatabase : RoomDatabase() {

    abstract fun newsDao(): NewsDao

    companion object {

        val DATABASE_NAME = "newsDatabase"
    }

}
