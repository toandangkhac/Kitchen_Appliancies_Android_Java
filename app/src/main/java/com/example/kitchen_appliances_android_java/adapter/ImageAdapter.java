package com.example.kitchen_appliances_android_java.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.kitchen_appliances_android_java.R;

import java.util.ArrayList;
import java.util.List;

public class ImageAdapter extends RecyclerView.Adapter<ImageAdapter.ImageViewHolder> {
    private static List<String> imagesUrl = new ArrayList<>();

    public ImageAdapter(Context context, List<String> imagesUrl) {
        this.imagesUrl = imagesUrl;

    }

    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_item, parent, false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        Log.d("ImageAdapter", "onBindViewHolder: " + imagesUrl.get(position));
        Glide.with(holder.ivProductImageItem.getContext()).load(imagesUrl.get(position)).into(holder.ivProductImageItem);
    }

    @Override
    public int getItemCount() {
        return imagesUrl.size();
    }


    static class ImageViewHolder extends RecyclerView.ViewHolder {
        ImageView ivProductImageItem;

        public ImageViewHolder(View itemView) {
            super(itemView);
            ivProductImageItem = itemView.findViewById(R.id.ivProductImageItem);

        }
    }
}
