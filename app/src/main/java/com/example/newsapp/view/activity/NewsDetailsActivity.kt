package com.example.newsapp.view.activity

import androidx.lifecycle.ViewModelProviders
import android.content.Context
import android.content.Intent
import androidx.databinding.DataBindingUtil
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast

import com.example.newsapp.R
import com.example.newsapp.data.model.NewsItem
import com.example.newsapp.databinding.ActivityNewsDetailsBinding
import com.example.newsapp.view.contractView.DetailContractView
import com.example.newsapp.view.viewModel.NewsDetailsVM

class NewsDetailsActivity : AppCompatActivity(), DetailContractView {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_news_details)

        val newsItem = intent.getParcelableExtra<NewsItem>(NEWS_ITEM_KEY)

        val viewModel = ViewModelProviders.of(this).get(NewsDetailsVM::class.java)
        viewModel.newsItem = newsItem
        viewModel.setContractView(this)

        val binding:ActivityNewsDetailsBinding = DataBindingUtil.setContentView(this, R.layout.activity_news_details)
        binding.setViewModel(viewModel)

    }

    override fun showToast(message: String) {
        Toast.makeText(application, message, Toast.LENGTH_SHORT).show()
    }

    companion object {
        var NEWS_ITEM_KEY = "newItemKey"

        fun newIntent(packageContext: Context, newsItem: NewsItem): Intent {
            val intent = Intent(packageContext, NewsDetailsActivity::class.java)
            intent.putExtra(NEWS_ITEM_KEY, newsItem)
            return intent
        }
    }
}
