package com.example.kitchen_appliances_android_java.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.kitchen_appliances_android_java.R;
import com.example.kitchen_appliances_android_java.adapter.ImageAdapter;
import com.example.kitchen_appliances_android_java.adapter.ProductAdapter;
import com.example.kitchen_appliances_android_java.api.ApiResponse;
import com.example.kitchen_appliances_android_java.api.TrustAllCertificatesSSLSocketFactory;
import com.example.kitchen_appliances_android_java.model.Category;
import com.example.kitchen_appliances_android_java.model.Image;
import com.example.kitchen_appliances_android_java.model.Product;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.ref.Reference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;


public class ProductDetailFragment extends Fragment {
    private int testCustomerId = 1;
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private HurlStack hurlStack;
    private Product product;
    private ArrayList<Image> tempList;

    private TextView tvQuantity, tvTotal, tvProductPrice, tvProductName, tvProductDescription, tvProductCategory;

    private Button btnDecreaseQuantity, btnIncreaseQuantity, btnAddToCart;
    private ArrayList<String> imagesUrl = new ArrayList<>();

    public ProductDetailFragment() {
        // Required empty public constructor
    }

    public static ProductDetailFragment newInstance(String param1, String param2) {
        ProductDetailFragment fragment = new ProductDetailFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.product_detail, container, false);

        rtHurlStack();
        product = (Product) getArguments().getSerializable("product");


        tvProductName = view.findViewById(R.id.tvProductName);
        tvProductDescription = view.findViewById(R.id.tvProductDescription);
        tvProductCategory = view.findViewById(R.id.tvProductCategory);
        tvProductPrice = view.findViewById(R.id.tvProductPrice);
        tvTotal = view.findViewById(R.id.tvTotal);
        tvQuantity = view.findViewById(R.id.tvQuantity);
        btnDecreaseQuantity = view.findViewById(R.id.btnDecreaseQuantity);
        btnIncreaseQuantity = view.findViewById(R.id.btnIncreaseQuantity);
        btnAddToCart = view.findViewById(R.id.btnAddToCart);

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
                SharedPreferences sharedPreferences = getContext().getSharedPreferences("MySharedPref", getContext().MODE_PRIVATE);
                int customerId = sharedPreferences.getInt("customerId", 0);
                addToCart(customerId, product.getId(), Integer.parseInt(tvQuantity.getText().toString()));
            }
        });


        return view;
    }
    private void getImages(int productId) {

        updateUI(imagesUrl);

    }
    private void fetchImagesForProduct(Product product) {
        RequestQueue queue = Volley.newRequestQueue(getContext(), hurlStack);
        String url = "https://10.0.2.2:7161/api/Image/product/" + product.getId();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> {
                    Gson gson = new Gson();
                    ApiResponse<ArrayList<Image>> apiResponse = gson.fromJson(response, new TypeToken<ApiResponse<ArrayList<Image>>>() {}.getType());
                    if (apiResponse != null && apiResponse.getStatus() == 200) {
                        tempList = apiResponse.getData();
                        for (Image image : tempList) {
                            imagesUrl.add(image.getUrl());
                        }
                        product.setImage(tempList.get(0).getUrl());
                        updateUI(imagesUrl);
                    } else {
//                        Toast.makeText(getContext(), "Error: Unable to fetch image data", Toast.LENGTH_SHORT).show();
                        Log.d("HomeFragment", "Error: Unable to fetch image data");
                    }
                }, error -> {
            error.printStackTrace();
            Log.d("HomeFragment", "Error: " + error.getMessage());
            Toast.makeText(getContext(), "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
        });
        queue.add(stringRequest);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
//        getImages(product.getId());
        fetchImagesForProduct(product);
    }

    private void updateUI(ArrayList<String> imagesUrl) {
        RecyclerView rvImages = getView().<RecyclerView>findViewById(R.id.imageRecycleView);
        ImageAdapter adapter = new ImageAdapter(getContext(), imagesUrl);
        rvImages.setAdapter(adapter);
        rvImages.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
    }
    private void addToCart(int customerId, int productId, int quantity) {
        // Handle the add to cart action here
        RequestQueue queue = Volley.newRequestQueue(getContext(), hurlStack);
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

                    Toast.makeText(getContext(), "Thêm vào giỏ hàng thành công!", Toast.LENGTH_SHORT).show();
                }, error -> {
            error.printStackTrace();
            Toast.makeText(getContext(), "Thêm vào giỏ hàng thất bại!", Toast.LENGTH_SHORT).show();
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
    private void updateTotal() {
        int quantity = Integer.parseInt(tvQuantity.getText().toString());
        double total = quantity * product.getPrice();
        tvTotal.setText("Tổng tiền: " +  String.valueOf(total) + " VNĐ");
    }
    private void getNameCategory(int categoryId){
        RequestQueue queue = Volley.newRequestQueue(getContext(), hurlStack);
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
//                            Toast.makeText(this, "Category: " + categoryName, Toast.LENGTH_SHORT).show();
                            tvProductCategory.setText("Loại: " + categoryName);
                        } else {
//                            Toast.makeText(this, "Error: Category data is null", Toast.LENGTH_SHORT).show();
                        }
                    }
                    else {
//                        Toast.makeText(this, "Error: Unable to fetch category data", Toast.LENGTH_SHORT).show();
                    }
                }, error -> {
            error.printStackTrace();
//            Toast.makeText(this, "Error at category: " + error.getMessage(), Toast.LENGTH_SHORT).show();
        });
        queue.add(stringRequest);
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
}