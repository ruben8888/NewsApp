package com.example.newsapp.view.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.databinding.ObservableBoolean

import com.example.newsapp.R
import com.example.newsapp.data.model.NewsItem
import com.example.newsapp.data.repository.NewsRepo
import com.example.newsapp.utils.DataUtils
import com.example.newsapp.view.contractView.DetailContractView

import io.reactivex.disposables.CompositeDisposable

class NewsDetailsVM(application: Application) : AndroidViewModel(application) {

    val isFavorite = ObservableBoolean()
    val isSavedItem = ObservableBoolean()
    private val compositeDisposable = CompositeDisposable()

     var newsItem: NewsItem = NewsItem()
        get() = field
        set(value) {
            field = value
            setNews(value)
        }

    private val newsRepo: NewsRepo
    private var contractView: DetailContractView? = null

    val articlePubicData: String
        get() = DataUtils.dataFormat(newsItem.webPublicationDate)

    init {
        newsRepo = NewsRepo

    }

    fun setNews(newsItem: NewsItem) {
        compositeDisposable.add(newsRepo.getNewsItemById(newsItem.id)
                .subscribe({ newsItem1 ->
                    isFavorite.set(newsItem1.isFavorite)
                    isSavedItem.set(newsItem1.isSaved)
                }, { throwable ->
                    isFavorite.set(false)
                    isSavedItem.set(false)
                }))


    }

    fun favoriteViewClick() {
        if (!isFavorite.get()) {
            newsItem.isFavorite = true
            newsItem.isSaved = isSavedItem.get()
            newsRepo.insert(newsItem)
            isFavorite.set(true)
            contractView!!.showToast(getApplication<Application>().getString(R.string.the_news_added_your_favorite))
        } else {
            newsItem.isFavorite = false
            newsItem.isSaved = isSavedItem.get()
            newsRepo.deleteFavorite(newsItem)
            isFavorite.set(false)
            contractView!!.showToast(getApplication<Application>().getString(R.string.the_news_deleted))
        }

    }

    fun saveViewClick() {
        if (!isSavedItem.get()) {
            newsItem.isSaved = true
            newsItem.isFavorite = isFavorite.get()
            newsRepo.insert(newsItem)
            isSavedItem.set(true)
            contractView!!.showToast(getApplication<Application>().getString(R.string.the_news_saved_for_offline_read))
        } else {
            newsItem.isSaved = false
            newsItem.isFavorite = isFavorite.get()
            newsRepo.deleteSaved(newsItem)
            isSavedItem.set(false)
            contractView!!.showToast(getApplication<Application>().getString(R.string.the_news_removed))
        }
    }

    fun setContractView(contractView: DetailContractView) {
        this.contractView = contractView
    }
}
