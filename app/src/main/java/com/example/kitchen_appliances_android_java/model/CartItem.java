package com.example.kitchen_appliances_android_java.model;

import androidx.annotation.NonNull;

public class CartItem {
    int customerId;
    int productId;
    int quantity;
    double price;



    public CartItem(int i, int i1, int i2) {
        this.customerId = i;
        this.productId = i1;
        this.quantity = i2;
    }
    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    @NonNull
    @Override
    public String toString() {
        return "CartItem{" +
                "customerId=" + customerId +
                ", productId=" + productId +
                ", quantity=" + quantity +
                ", price=" + price +
                '}';
    }
}
