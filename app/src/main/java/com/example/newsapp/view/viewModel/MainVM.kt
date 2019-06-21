package com.example.newsapp.view.viewModel

import android.app.Application
import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableField
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.example.newsapp.NewsApp
import com.example.newsapp.R
import com.example.newsapp.data.model.NewsItem
import com.example.newsapp.data.repository.NewsRepo
import com.example.newsapp.utils.Constants
import com.example.newsapp.utils.Constants.NEWS_UPDATE_TIME_SECOND
import com.example.newsapp.utils.DataUtils
import com.example.newsapp.utils.NetworkUtils
import com.example.newsapp.utils.threads.ExecutorService
import com.example.newsapp.utils.threads.ExecutorType
import com.example.newsapp.view.adapters.PaginationScrollAdapter
import com.example.newsapp.view.contractView.MainContractView
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import java.util.*
import java.util.concurrent.TimeUnit

class MainVM(application: Application) : AndroidViewModel(application), PaginationScrollAdapter.OnLoadMoreListener, LifecycleObserver {

    private val disposable = CompositeDisposable()
    private var disposableInterval: Disposable? = null

    val showProgressBar = ObservableBoolean()
    val showFavoritesHeader = ObservableBoolean()
    val headerNewsText = ObservableField<String>()

    private var contractView: MainContractView? = null
    private val newsRepo: NewsRepo
    private var pageNews = 1
    private val favouriteItems: MutableList<NewsItem>
    private val newsItems: MutableList<NewsItem>

    private var isShownSavedData: Boolean = false

    private val firstNewsPublishDate: String?
        get() = NewsApp.instance.preferences?.getString(Constants.FIRST_NEWS_PUBLISH_DATE, "")

    init {
        newsRepo = NewsRepo
        favouriteItems = ArrayList()
        newsItems = ArrayList()
        getFavorites()
        loadNews(false)
    }

    private fun getFavorites() {
        disposable.add(newsRepo.allFavorite
                .subscribe({ favourites ->
                    if (favourites.isEmpty()) {
                        showFavoritesHeader.set(false)
                    } else {
                        showFavoritesHeader.set(true)
                    }
                    if (contractView != null) {
                        favouriteItems.clear()
                        favourites.reversed()
                        favouriteItems.addAll(favourites)
                        contractView!!.notifyFavouriteData()
                    }
                }, { throwable -> contractView!!.showToast(getApplication<Application>().getString(R.string.error)) }))
    }

    private fun loadNews(isLoadMore: Boolean) {
        if (NetworkUtils.isNetworkAvailable(getApplication())) {
            headerNewsText.set(getApplication<Application>().getString(R.string.last_news))

            disposable.add(newsRepo.getNews(pageNews)
                    .doOnSubscribe { disposable -> showProgressBar.set(true) }
                    .retry(2)
                    .doOnSuccess { response -> showProgressBar.set(false) }
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({ response ->
                        if (response.status.equals(getApplication<Application>().getString(R.string.ok), ignoreCase = true)) {
                            newsItems.addAll(response.newsItems)
                            contractView!!.notifyNewsData()
                            if (!isLoadMore) {
                                setPublishDateFirstNewsForUpdate()
                            }
                            if (response.currentPage == response.pages) {
                                contractView!!.setLastPage()
                            }
                        } else {
                            contractView!!.showToast(getApplication<Application>().getString(R.string.error))
                        }
                    }, { throwable ->
                        showProgressBar.set(false)
                        contractView!!.showToast(getApplication<Application>().getString(R.string.error))
                    }))

        } else {
            showSavedNews()
        }
    }

    private fun registerTimerForUpdateNews() {
        disposableInterval = Observable
                .interval(NEWS_UPDATE_TIME_SECOND.toLong(), TimeUnit.SECONDS)
                .flatMap { aLong -> newsRepo.getNewsAfterLastDate(firstNewsPublishDate!!).toObservable() }
                .filter { newsItems -> !newsItems.isEmpty() }
                .subscribe({ findNewNewsItem(it) })
    }


    private fun setPublishDateFirstNewsForUpdate() {
        if (!newsItems.isEmpty()) {
            val lastItemDateString = newsItems[0].webPublicationDate
            val incrementedDate = DataUtils.incrementSeconds(lastItemDateString)
            NewsApp.instance.preferences?.apply {
                edit().putString(Constants.FIRST_NEWS_PUBLISH_DATE, incrementedDate).apply()
            }
        }
    }


    private fun findNewNewsItem(items: List<NewsItem>) {

        val foundData = ArrayList<NewsItem>()

        for (item in items) {
            //delete item if already exist
            newsItems.remove(item)

            foundData.add(item)
        }
        if (!foundData.isEmpty()) {
            newsItems.addAll(0, foundData)
            setPublishDateFirstNewsForUpdate()
            updateUI()
        }
    }

    private fun updateUI() {
        ExecutorService.getExecutorProvider()
                .getExecutor(ExecutorType.MAIN)
                .execute { contractView!!.notifyNewsData() }
    }

    fun showSavedNews() {
        if (!isShownSavedData) {
            disposable.add(newsRepo.savedNews.subscribe { savedNews ->
                if (!savedNews.isEmpty()) {
                    isShownSavedData = true
                    newsItems.clear()
                    newsItems.addAll(savedNews)
                    contractView!!.notifyNewsData()
                } else {
                    contractView!!.showToast(getApplication<Application>().getString(R.string.you_havent_saved_news_yet))
                }
                headerNewsText.set(getApplication<Application>().getString(R.string.your_saved_news))

            })
        } else {
            newsItems.clear()
            isShownSavedData = false
            loadNews(false)
        }

    }

    override fun onCleared() {
        super.onCleared()
        disposable.dispose()
    }

    override fun onLoadMore() {
        if (!isShownSavedData) {
            pageNews++
            loadNews(true)
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun onPause() {
        if (disposableInterval != null)
            disposableInterval!!.dispose()
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun onResume() {
        if (NetworkUtils.isNetworkAvailable(getApplication())) {
            registerTimerForUpdateNews()
        }
    }

    fun setContractView(contractView: MainContractView) {
        this.contractView = contractView
    }

    fun getFavouriteItems(): List<NewsItem> {
        return favouriteItems
    }

    fun getNewsItems(): List<NewsItem> {
        return newsItems
    }
}
