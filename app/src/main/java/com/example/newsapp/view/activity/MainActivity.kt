package com.example.newsapp.view.activity

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.core.view.ViewCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProviders
import com.example.newsapp.R
import com.example.newsapp.data.model.NewsItem
import com.example.newsapp.databinding.ActivityMainBinding
import com.example.newsapp.view.adapters.FavoriteAdapter
import com.example.newsapp.view.adapters.NewsAdapter
import com.example.newsapp.view.contractView.MainContractView
import com.example.newsapp.view.viewModel.MainVM
import java.util.*

class MainActivity : AppCompatActivity(), MainContractView {

    private var binding: ActivityMainBinding? = null
    private var viewModel: MainVM? = null
    private var newsAdapter: NewsAdapter? = null
    private var favoriteAdapter: FavoriteAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewModel = ViewModelProviders.of(this).get(MainVM::class.java)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        lifecycle.addObserver(viewModel!!)
        binding!!.viewModel = viewModel
        viewModel!!.setContractView(this)
        initAdapters()
        if (savedInstanceState != null) {
            resetDataAfterRotate()
        }
    }

    private fun initAdapters() {
        val veriticalRV = binding!!.verticalRV
        newsAdapter = NewsAdapter(viewModel!!.getNewsItems())
        veriticalRV.adapter = newsAdapter
        newsAdapter!!.setOnItemClickListener { item, view -> this.openDetailActivity(item, view) }
        newsAdapter!!.setOnLoadMoreListener(viewModel)

        val horizontalRV = binding!!.horizontalRV
        favoriteAdapter = FavoriteAdapter(viewModel!!.getFavouriteItems())
        horizontalRV.adapter = favoriteAdapter
        favoriteAdapter!!.setOnItemClickListener { item, view -> this.openDetailActivity(item, view) }
    }

    override fun notifyNewsData() {
        newsAdapter!!.notifyData()
    }

    override fun notifyFavouriteData() {
        favoriteAdapter!!.notifyDataSetChanged()
    }

    override fun showToast(message: String) {
        Toast.makeText(application, message, Toast.LENGTH_SHORT).show()
    }

    override fun setLastPage() {
        newsAdapter!!.setLastPage(true)
    }

    private fun openDetailActivity(item: NewsItem, view: View) {
        val intent = NewsDetailsActivity.newIntent(this, item)
        val options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                this,
                view,
                ViewCompat.getTransitionName(view).toString())
        startActivity(intent, options.toBundle())
    }

    private fun resetDataAfterRotate() {
        notifyFavouriteData()
        notifyNewsData()
    }
}
