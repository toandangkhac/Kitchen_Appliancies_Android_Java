package com.example.kitchen_appliances_android_java.adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kitchen_appliances_android_java.R;
import com.example.kitchen_appliances_android_java.activity.ProductDetail;
import com.example.kitchen_appliances_android_java.model.Product;

import java.util.ArrayList;

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

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            tvProductPrice = itemView.findViewById(R.id.tvProductPrice);
            tvProductName = itemView.findViewById(R.id.tvProductName);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        Product clickedProduct = products.get(position);
                        Intent intent = new Intent(v.getContext(), ProductDetail.class);
                        intent.putExtra("product", clickedProduct);
                        v.getContext().startActivity(intent);
                    }
                }
            });
        }

        public void bind(Product product) {
            tvProductPrice.setText(String.valueOf(product.getPrice()));
            tvProductName.setText(product.getName());
        }
    }
}