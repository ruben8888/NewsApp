package com.example.newsapp.view.adapters;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.example.newsapp.R;
import com.example.newsapp.data.model.NewsItem;

import java.util.List;

public class FavoriteAdapter extends RecyclerView.Adapter<FavoriteAdapter.CustomVH> {

    private List<NewsItem> newsItems;
    private OnItemClickListener onItemClickListener;

    public FavoriteAdapter(List<NewsItem> newsItems) {
        this.newsItems = newsItems;
    }

    @NonNull
    @Override
    public CustomVH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View contactView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_favorite, parent, false);
        return new CustomVH(contactView);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomVH customVH, int position) {
        NewsItem newsItem = newsItems.get(position);
        customVH.titleTv.setText(newsItem.getSectionName());
        customVH.subtitleTv.setText(newsItem.getWebTitle());

        loadImage(customVH.avatarImg, newsItem);
        customVH.itemView.setOnClickListener(v -> {
            if (onItemClickListener != null)
                onItemClickListener.onItemClick(newsItem, customVH.avatarImg);
        });
    }


    @Override
    public int getItemCount() {
        return newsItems != null ? newsItems.size() : 0;
    }

    class CustomVH extends RecyclerView.ViewHolder {
        ImageView avatarImg;
        TextView titleTv;
        TextView subtitleTv;

        CustomVH(@NonNull View itemView) {
            super(itemView);
            avatarImg = itemView.findViewById(R.id.imageView);
            titleTv = itemView.findViewById(R.id.title);
            subtitleTv = itemView.findViewById(R.id.subtitle);
        }
    }

    private void loadImage(@NonNull ImageView imageView, NewsItem newsItem) {
        if (newsItem.getFields() != null) {
            String url = newsItem.getFields().getThumbnail();
            Glide.with(imageView.getContext())
                    .load(url)
                    .apply(new RequestOptions().diskCacheStrategy(DiskCacheStrategy.ALL))
                    .into(imageView);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(NewsItem item, View view);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

}
