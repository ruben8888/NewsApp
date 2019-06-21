package com.example.newsapp.data.repository

import com.example.newsapp.NewsApp
import com.example.newsapp.data.APIClient.RetrofitClient
import com.example.newsapp.data.database.dao.NewsDao
import com.example.newsapp.data.model.NewsItem
import com.example.newsapp.data.model.NewsResponse
import com.example.newsapp.utils.Constants
import com.example.newsapp.utils.threads.ExecutorProvider
import com.example.newsapp.utils.threads.ExecutorService
import com.example.newsapp.utils.threads.ExecutorType
import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.util.concurrent.Executor

object NewsRepo {
    private val newsDao: NewsDao
    val savedNews: Single<List<NewsItem>>
        get() = newsDao.allSavedNews
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())

    val allFavorite: Flowable<List<NewsItem>>
        get() = newsDao.allFavorite
                .observeOn(AndroidSchedulers.mainThread())

    private val executorProvider: ExecutorProvider
        get() = ExecutorService.getExecutorProvider()

    init {
        newsDao = NewsApp.instance.database!!.newsDao()
    }

    fun getNews(page: Int): Single<NewsResponse> {
        return RetrofitClient
                .apiService
                .getAllNews(Constants.API_KEY, page, "thumbnail,bodyText")
                .map { it.response }
                .subscribeOn(Schedulers.io())

    }

    fun getNewsAfterLastDate(lastData: String): Single<List<NewsItem>> {
        return RetrofitClient
                .apiService
                .getNewsAfterLastDate(Constants.API_KEY, lastData, "thumbnail,bodyText")
                .map { news -> news.response.newsItems }
                .subscribeOn(Schedulers.io())

    }

    fun getNewsItemById(id: String): Single<NewsItem> {
        return newsDao.getNewsItemById(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

    fun insert(newsItem: NewsItem) {
        getExecutor(ExecutorType.DB_COMMUNICATION).execute { newsDao.insert(newsItem) }
    }

    fun deleteFavorite(item: NewsItem) {
        getExecutor(ExecutorType.DB_COMMUNICATION).execute {
            if (!item.isSaved) {
                newsDao.delete(item.id)
            } else {
                newsDao.update(item)
            }

        }
    }

    fun deleteSaved(item: NewsItem) {
        getExecutor(ExecutorType.DB_COMMUNICATION).execute {
            if (!item.isFavorite) {
                newsDao.delete(item.id)
            } else {
                newsDao.update(item)
            }

        }
    }

    private fun getExecutor(@ExecutorType type: Int): Executor {
        return executorProvider.getExecutor(type)
    }

}
