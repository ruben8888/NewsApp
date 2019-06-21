package com.example.newsapp.view.activity;

import androidx.lifecycle.ViewModelProviders;
import android.content.Intent;
import androidx.databinding.DataBindingUtil;
import android.os.Bundle;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.view.ViewCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.Toast;

import com.example.newsapp.R;
import com.example.newsapp.data.model.NewsItem;
import com.example.newsapp.databinding.ActivityMainBinding;
import com.example.newsapp.view.adapters.FavoriteAdapter;
import com.example.newsapp.view.adapters.NewsAdapter;
import com.example.newsapp.view.contractView.MainContractView;
import com.example.newsapp.view.viewModel.MainVM;

import java.util.Objects;

public class MainActivity extends AppCompatActivity implements MainContractView {

    private ActivityMainBinding binding;
    private MainVM viewModel;
    private NewsAdapter newsAdapter;
    private FavoriteAdapter favoriteAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewModel = ViewModelProviders.of(this).get(MainVM.class);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        getLifecycle().addObserver(viewModel);
        binding.setViewModel(viewModel);
        viewModel.setContractView(this);
        initAdapters();
        if (savedInstanceState != null) {
            resetDataAfterRotate();
        }
    }

    private void initAdapters() {
        RecyclerView veriticalRV = binding.verticalRV;
        newsAdapter = new NewsAdapter(viewModel.getNewsItems());
        veriticalRV.setAdapter(newsAdapter);
        newsAdapter.setOnItemClickListener(this::openDetailActivity);
        newsAdapter.setOnLoadMoreListener(viewModel);

        RecyclerView horizontalRV = binding.horizontalRV;
        favoriteAdapter = new FavoriteAdapter(viewModel.getFavouriteItems());
        horizontalRV.setAdapter(favoriteAdapter);
        favoriteAdapter.setOnItemClickListener(this::openDetailActivity);
    }

    @Override
    public void notifyNewsData() {
        newsAdapter.notifyData();
    }

    @Override
    public void notifyFavouriteData() {
        favoriteAdapter.notifyDataSetChanged();
    }

    @Override
    public void showToast(String message) {
        Toast.makeText(getApplication(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void setLastPage() {
        newsAdapter.setLastPage(true);
    }

    private void openDetailActivity(NewsItem item, View view) {
        Intent intent = NewsDetailsActivity.newIntent(this, item);
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(
                this,
                view,
                Objects.requireNonNull(ViewCompat.getTransitionName(view)));
        startActivity(intent, options.toBundle());
    }

    private void resetDataAfterRotate() {
        if (viewModel.getFavouriteItems() != null) {
            notifyFavouriteData();
        }
        if (viewModel.getNewsItems() != null) {
            notifyNewsData();
        }
    }
}
