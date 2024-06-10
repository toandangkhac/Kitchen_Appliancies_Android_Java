package com.example.kitchen_appliances_android_java.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.kitchen_appliances_android_java.R;
import com.example.kitchen_appliances_android_java.api.TrustAllCertificatesSSLSocketFactory;
import com.example.kitchen_appliances_android_java.model.ApiResponse;
import com.example.kitchen_appliances_android_java.model.Category;
import com.example.kitchen_appliances_android_java.model.Product;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;

public class ProductDetail extends AppCompatActivity {
    private int testCustomerId = 1;

    private HurlStack hurlStack;
    private Product product;
    private TextView tvQuantity, tvTotal, tvProductPrice, tvProductName, tvProductDescription, tvProductCategory;
    private ImageView ivProductImage;
    private Button btnDecreaseQuantity, btnIncreaseQuantity, btnAddToCart;

    @Override
    @SuppressLint("MissingInflatedId")
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.product_detail);

        rtHurlStack();
        product = (Product) getIntent().getSerializableExtra("product");

        ivProductImage = findViewById(R.id.ivProductImage);
        tvProductName = findViewById(R.id.tvProductName);
        tvProductDescription = findViewById(R.id.tvProductDescription);
        tvProductCategory = findViewById(R.id.tvProductCategory);
        tvProductPrice = findViewById(R.id.tvProductPrice);
        tvTotal = findViewById(R.id.tvTotal);
        tvQuantity = findViewById(R.id.tvQuantity);
        btnDecreaseQuantity = findViewById(R.id.btnDecreaseQuantity);
        btnIncreaseQuantity = findViewById(R.id.btnIncreaseQuantity);
        btnAddToCart = findViewById(R.id.btnAddToCart);

        tvProductName.setText(product.getName());
        tvProductDescription.setText("Mô tả: " + product.getDescription());
        tvProductPrice.setText("Giá: " + String.valueOf(product.getPrice() + " VNĐ"));
        getNameCategory(product.getCategoryId());
        updateTotal();


        btnDecreaseQuantity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                decreaseQuantity();
                updateTotal();
            }
        });

        btnIncreaseQuantity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                increaseQuantity();
                updateTotal();
            }
        });

        btnAddToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addToCart(testCustomerId, product.getId(), Integer.parseInt(tvQuantity.getText().toString()));
            }
        });


    }

    public void rtHurlStack() {
        hurlStack = new HurlStack() {
            @Override
            protected HttpURLConnection createConnection(URL url) throws IOException {
                HttpsURLConnection httpsURLConnection = (HttpsURLConnection) super.createConnection(url);
                try {
                    httpsURLConnection.setSSLSocketFactory(new TrustAllCertificatesSSLSocketFactory(null));
                    httpsURLConnection.setHostnameVerifier(new HostnameVerifier() {
                        @Override
                        public boolean verify(String hostname, SSLSession session) {
                            // Ignore host name verification. It returns true for all host names.
                            return true;
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return httpsURLConnection;
            }
        };
    }

    private void updateTotal() {
        int quantity = Integer.parseInt(tvQuantity.getText().toString());
        double total = quantity * product.getPrice();
        tvTotal.setText("Tổng tiền: " +  String.valueOf(total) + " VNĐ");
    }

    private void getNameCategory(int categoryId){
        RequestQueue queue = Volley.newRequestQueue(this, hurlStack);
        String url = "https://10.0.2.2:7161/api/Category/" + categoryId;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> {
                    Gson gson = new Gson();
                    ApiResponse<Category> apiResponse = gson.fromJson(response, new TypeToken<ApiResponse<Category>>() {}.getType());
                    if (apiResponse != null && apiResponse.getStatus() == 200) {
                        Category category = apiResponse.getData();
                        if (category != null) {
                            // Xử lý dữ liệu category
                            String categoryName = category.getName();
                            Toast.makeText(this, "Category: " + categoryName, Toast.LENGTH_SHORT).show();
                            tvProductCategory.setText("Loại: " + categoryName);
                        } else {
                            Toast.makeText(this, "Error: Category data is null", Toast.LENGTH_SHORT).show();
                        }
                    }
                    else {
                        Toast.makeText(this, "Error: Unable to fetch category data", Toast.LENGTH_SHORT).show();
                    }
                }, error -> {
            error.printStackTrace();
            Toast.makeText(this, "Error at category: " + error.getMessage(), Toast.LENGTH_SHORT).show();
        });
        queue.add(stringRequest);
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

    private void addToCart(int customerId, int productId, int quantity) {
        // Handle the add to cart action here
        RequestQueue queue = Volley.newRequestQueue(this, hurlStack);
        String url = "https://10.0.2.2:7161/api/CartDetail";

        JSONObject requestData = new JSONObject();
        try {
            requestData.put("customerId", customerId);
            requestData.put("productId", productId);
            requestData.put("quantity", quantity);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                response -> {
                    //Xu ly phan hoi thanh cong

                    Toast.makeText(this, "Thêm vào giỏ hàng thành công!", Toast.LENGTH_SHORT).show();
                }, error -> {
            error.printStackTrace();
            Toast.makeText(this, "Thêm vào giỏ hàng thất bại!", Toast.LENGTH_SHORT).show();
        }) {
            @Override
            public byte[] getBody() {
                return requestData.toString().getBytes();
            }

            @Override
            public String getBodyContentType() {
                return "application/json";
            }
        };

        queue.add(stringRequest);
    }
}