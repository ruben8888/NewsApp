package com.example.newsapp.utils

import android.content.Context
import android.util.Log
import androidx.work.*

import com.example.newsapp.data.repository.NewsRepo
import com.example.newsapp.NewsApp

import java.util.concurrent.TimeUnit

import io.reactivex.disposables.Disposable

import com.example.newsapp.utils.Constants.BACKGROUND_CHECK_NEWS_TIME_MINUTE
import com.example.newsapp.utils.Constants.WORK_TAG

class CheckNewDataWorker(context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {

    private var diposible: Disposable? = null

    private val firstNewsPublishDate: String?
        get() = NewsApp.instance.preferences!!.getString(Constants.FIRST_NEWS_PUBLISH_DATE, "")

    override fun doWork(): Result {
        if (NetworkUtils.isNetworkAvailable(applicationContext)) {
            diposible = NewsRepo
                    .getNewsAfterLastDate(firstNewsPublishDate!!)
                    .filter { newsItems -> !newsItems.isEmpty() }
                    .map { newsItems -> newsItems[0] }
                    .subscribe({ newsItem ->
                        val publicationDate = newsItem.webPublicationDate
                        setPublishDateFirstNewsForUpdate(publicationDate)
                        NotificationUtils.sendNotification(applicationContext)

                    }, { throwable -> Log.e("error", throwable.toString()) })
            startCheckNewDataPeriodicWorker()
        }
        return Result.success()
    }


    private fun setPublishDateFirstNewsForUpdate(publicationDate: String) {
        val incrementedDate = DataUtils.incrementSeconds(publicationDate)
        NewsApp.instance.preferences!!.edit().putString(Constants.FIRST_NEWS_PUBLISH_DATE, incrementedDate).apply()
    }

    override fun onStopped() {
        super.onStopped()
        if (diposible != null)
            diposible!!.dispose()
    }

    companion object {
        fun startCheckNewDataPeriodicWorker() {
            val constraints = Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()

            val workRequest = OneTimeWorkRequest.Builder(CheckNewDataWorker::class.java)
                    .addTag(WORK_TAG)
                    .setInitialDelay(BACKGROUND_CHECK_NEWS_TIME_MINUTE.toLong(), TimeUnit.MINUTES)
                    .setConstraints(constraints)
                    .build()
            WorkManager.getInstance().enqueue(workRequest)
        }

        fun cancelWork() {
            WorkManager.getInstance().cancelAllWorkByTag(WORK_TAG)
        }
    }
}
