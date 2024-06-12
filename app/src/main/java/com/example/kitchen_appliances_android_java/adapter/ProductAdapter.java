package com.example.kitchen_appliances_android_java.adapter;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.kitchen_appliances_android_java.R;
import com.example.kitchen_appliances_android_java.activity.ProductDetail;
import com.example.kitchen_appliances_android_java.fragment.ProductDetailFragment;
import com.example.kitchen_appliances_android_java.model.Product;

import java.util.ArrayList;
import java.util.Map;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {
    private static ArrayList<Product> products;

    public ProductAdapter(ArrayList<Product> products) {
        this.products = products;

    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = products.get(position);
        holder.bind(product);
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    static class ProductViewHolder extends RecyclerView.ViewHolder {
        TextView  tvProductPrice, tvProductName;
        ImageView ivProductImage;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            tvProductPrice = itemView.findViewById(R.id.tvProductPrice);
            tvProductName = itemView.findViewById(R.id.tvProductName);
            ivProductImage = itemView.findViewById(R.id.ivProductImage1);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        Product clickedProduct = products.get(position);
                        ProductDetailFragment fragment = new ProductDetailFragment();
                        Bundle bundle = new Bundle();
                        bundle.putSerializable("product", clickedProduct);
                        fragment.setArguments(bundle);
                        ((AppCompatActivity) v.getContext()).getSupportFragmentManager().beginTransaction().replace(R.id.appHostFragment, fragment).commit();
                    }
                }
            });
        }

        public void bind(Product product) {
            tvProductPrice.setText(String.valueOf(product.getPrice()));
            tvProductName.setText(product.getName());
            Glide.with(itemView.getContext()).load(product.getImage()).into(ivProductImage);
        }
    }
}