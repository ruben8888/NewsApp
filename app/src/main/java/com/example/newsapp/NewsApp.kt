package com.example.newsapp

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.room.Room
import com.example.newsapp.data.database.NewsDatabase
import com.example.newsapp.utils.CheckNewDataWorker
import com.example.newsapp.utils.CheckNewDataWorker.Companion.startCheckNewDataPeriodicWorker
import com.example.newsapp.utils.Constants.PREFERENCE_NAME

class NewsApp : Application(), LifecycleObserver {
    var preferences: SharedPreferences? = null
        private set
    var database: NewsDatabase? = null
        private set


    override fun onCreate() {
        super.onCreate()
        instance = this
        database = Room.databaseBuilder(this, NewsDatabase::class.java, NewsDatabase.DATABASE_NAME)
                .build()
        preferences = getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE)
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    internal fun onAppBackgrounded() {
        startCheckNewDataPeriodicWorker()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    internal fun onAppForegrounded() {
        CheckNewDataWorker.cancelWork()
    }

    companion object {

        lateinit var instance: NewsApp
    }
}
