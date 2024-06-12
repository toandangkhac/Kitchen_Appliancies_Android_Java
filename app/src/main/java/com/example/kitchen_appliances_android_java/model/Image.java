package com.example.kitchen_appliances_android_java.model;

public class Image {
    private int id;
    private String url;
    private int productId;

    public Image(int id, String url, int productId) {
        this.id = id;
        this.url = url;
        this.productId = productId;
    }

    public int getId() {
        return id;
    }

    public String getUrl() {
        return url;
    }

    public int getProductId() {
        return productId;
    }
}
