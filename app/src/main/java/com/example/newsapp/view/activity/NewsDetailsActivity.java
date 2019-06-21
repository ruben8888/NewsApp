package com.example.newsapp.view.activity;

import androidx.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
import androidx.databinding.DataBindingUtil;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.example.newsapp.R;
import com.example.newsapp.data.model.NewsItem;
import com.example.newsapp.databinding.ActivityNewsDetailsBinding;
import com.example.newsapp.view.contractView.DetailContractView;
import com.example.newsapp.view.viewModel.NewsDetailsVM;

public class NewsDetailsActivity extends AppCompatActivity implements DetailContractView {
    public static String NEWS_ITEM_KEY = "newItemKey";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_details);

        NewsItem newsItem = getIntent().getParcelableExtra(NEWS_ITEM_KEY);

        NewsDetailsVM viewModel = ViewModelProviders.of(this).get(NewsDetailsVM.class);
        viewModel.setNewsItem(newsItem);
        viewModel.setContractView(this);

        ActivityNewsDetailsBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_news_details);
        binding.setViewModel(viewModel);

    }

    public static Intent newIntent(Context packageContext, NewsItem newsItem) {
        Intent intent = new Intent(packageContext, NewsDetailsActivity.class);
        intent.putExtra(NEWS_ITEM_KEY,newsItem);
        return intent;
    }

    @Override
    public void showToast(String message) {
        Toast.makeText(getApplication(), message, Toast.LENGTH_SHORT).show();
    }
}
