package com.example.kitchen_appliances_android_java.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.kitchen_appliances_android_java.R;
import com.example.kitchen_appliances_android_java.model.Product;

public class ProductDetail extends AppCompatActivity {
    private Product product;
    private TextView tvQuantity;

    @Override
    @SuppressLint("MissingInflatedId")
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.product_detail);

        product = (Product) getIntent().getSerializableExtra("product");

        ImageView ivProductImage = findViewById(R.id.ivProductImage);
        TextView tvProductName = findViewById(R.id.tvProductName);
        TextView tvProductDescription = findViewById(R.id.tvProductDescription);
        TextView tvProductCategory = findViewById(R.id.tvProductCategory);
        TextView tvProductPrice = findViewById(R.id.tvProductPrice);
        tvQuantity = findViewById(R.id.tvQuantity);
        Button btnDecreaseQuantity = findViewById(R.id.btnDecreaseQuantity);
        Button btnIncreaseQuantity = findViewById(R.id.btnIncreaseQuantity);
        Button btnAddToCart = findViewById(R.id.btnAddToCart);
        tvProductName.setText(product.getName());
        tvProductDescription.setText(product.getDescription());
        tvProductCategory.setText(String.valueOf(product.getCategoryId()));
        tvProductPrice.setText(String.valueOf(product.getPrice()));

        btnDecreaseQuantity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                decreaseQuantity();
            }
        });

        btnIncreaseQuantity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                increaseQuantity();
            }
        });

        btnAddToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addToCart();
            }
        });
    }

    private void decreaseQuantity() {
        int quantity = Integer.parseInt(tvQuantity.getText().toString());
        if (quantity > 1) {
            tvQuantity.setText(String.valueOf(quantity - 1));
        }
    }

    private void increaseQuantity() {
        int quantity = Integer.parseInt(tvQuantity.getText().toString());
        tvQuantity.setText(String.valueOf(quantity + 1));
    }

    private void addToCart() {
        // Handle the add to cart action here
    }
}