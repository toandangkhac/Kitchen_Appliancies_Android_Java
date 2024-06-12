package com.example.kitchen_appliances_android_java.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.kitchen_appliances_android_java.R;
import com.example.kitchen_appliances_android_java.api.ApiService;
import com.example.kitchen_appliances_android_java.model.CartItem;
import com.example.kitchen_appliances_android_java.model.Product;

import java.util.ArrayList;

public class CartItemAdapter extends RecyclerView.Adapter<CartItemAdapter.CartViewHolder> {
    private ArrayList<CartItem> listCartItem;
    private static ApiService apiService;
    private static OnQuantityChangeListener onQuantityChangeListener;
    public interface OnQuantityChangeListener {
        void onQuantityChange(double priceDifference);
    }
    public CartItemAdapter(ArrayList<CartItem> listCartItem, Context context, OnQuantityChangeListener onQuantityChangeListener) {
        this.listCartItem = listCartItem;
        this.apiService = new ApiService(context);
        this.onQuantityChangeListener = onQuantityChangeListener;
    }
    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_item, parent, false);
        return new CartViewHolder(view, onQuantityChangeListener);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        CartItem cartItem = listCartItem.get(position);
        holder.bind(cartItem);
        cartItem.setPrice(cartItem.getQuantity() * cartItem.getPrice());
    }

    @Override
    public int getItemCount() {
        return listCartItem.size();
    }

    public static class CartViewHolder extends RecyclerView.ViewHolder {
        // Views in the cart item layout
        ImageView ivProductImage;
        TextView tvProductName, tvProductPrice, tvProductQuantity;
        Button btnDecrease, btnIncrease;

        public CartViewHolder(@NonNull View itemView, OnQuantityChangeListener onQuantityChangeListener1) {
            super(itemView);
            onQuantityChangeListener = onQuantityChangeListener1;
            // Initialize the views
            ivProductImage = itemView.findViewById(R.id.ivProductImage1);
            tvProductName = itemView.findViewById(R.id.tvProductName);
            tvProductPrice = itemView.findViewById(R.id.tvProductPrice);
            tvProductQuantity = itemView.findViewById(R.id.tvQuantity);
            btnDecrease = itemView.findViewById(R.id.btnDecrease);
            btnIncrease = itemView.findViewById(R.id.btnIncrease);
        }

        public void bind(CartItem cartItem) {
            apiService.loadProductById(cartItem.getProductId(), new ApiService.SingleProductCallback() {
                @Override
                public void onSuccess(Product product) {
                    tvProductName.setText(product.getName());
                    tvProductPrice.setText(String.valueOf(product.getPrice()));
                    tvProductQuantity.setText(String.valueOf(cartItem.getQuantity()));
                    Glide.with(itemView.getContext()).load(product.getImage()).into(ivProductImage);
                    cartItem.setPrice(product.getPrice() * cartItem.getQuantity());
                    btnDecrease.setOnClickListener(v -> {
                        int quantity = cartItem.getQuantity();
                        if (quantity > 1) {
                            quantity--;
                            cartItem.setQuantity(quantity);
                            tvProductQuantity.setText(String.valueOf(quantity));
                            double oldPrice = cartItem.getPrice();
                            double newPrice = product.getPrice() * cartItem.getQuantity();
                            cartItem.setPrice(newPrice);
                            onQuantityChangeListener.onQuantityChange(newPrice - oldPrice);
                        }
                    });
                    btnIncrease.setOnClickListener(v -> {
                        int quantity = cartItem.getQuantity();
                        quantity++;
                        cartItem.setQuantity(quantity);
                        tvProductQuantity.setText(String.valueOf(quantity));
                        double oldPrice = cartItem.getPrice();
                        double newPrice = product.getPrice() * cartItem.getQuantity();
                        cartItem.setPrice(newPrice);
                        onQuantityChangeListener.onQuantityChange(newPrice - oldPrice);
                    });
                }

                @Override
                public void onError(Exception e) {
                    e.printStackTrace();
                }
            });
        }

    }
}
