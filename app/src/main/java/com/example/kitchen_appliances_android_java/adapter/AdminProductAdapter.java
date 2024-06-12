package com.example.kitchen_appliances_android_java.adapter;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.kitchen_appliances_android_java.R;
import com.example.kitchen_appliances_android_java.activity.EditProductActivity;
import com.example.kitchen_appliances_android_java.model.Product;

import java.util.ArrayList;

public class AdminProductAdapter extends RecyclerView.Adapter<AdminProductAdapter.AdminProductViewHolder> {
    private static ArrayList<Product> products;

    public AdminProductAdapter(ArrayList<Product> products) {
        this.products = products;
    }

    @NonNull
    @Override
    public AdminProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.product_admin, parent, false);
        return new AdminProductViewHolder(view);
    }



    @Override
    public void onBindViewHolder(@NonNull AdminProductViewHolder holder, int position) {
        Product product = products.get(position);
        holder.bind(product);
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    static class AdminProductViewHolder extends RecyclerView.ViewHolder{
        TextView product_name, product_price;
        ImageView product_image;
        ImageButton btn_delete_product, btn_edit_product;

        public AdminProductViewHolder(@NonNull View itemView) {
            super(itemView);
            product_name = itemView.findViewById(R.id.product_name);
            product_price = itemView.findViewById(R.id.product_price);
            product_image = itemView.findViewById(R.id.product_image);
            btn_delete_product = itemView.findViewById(R.id.btn_delete_product);
            btn_edit_product = itemView.findViewById(R.id.btn_edit_product);

            btn_edit_product.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        Product clickedProduct = products.get(position);
                        Intent intent = new Intent(v.getContext(), EditProductActivity.class);
                        intent.putExtra("product", clickedProduct);
                        v.getContext().startActivity(intent);
                    }
                }
            });
            btn_delete_product.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        }

        public void bind(Product product) {
            product_name.setText(product.getName());
            product_price.setText(String.valueOf(product.getPrice()));
        }
    }


}
