package com.example.kitchen_appliances_android_java.api;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.kitchen_appliances_android_java.Request.CreateCustomerRequest;
import com.example.kitchen_appliances_android_java.Request.ResendOTPRequest;
import com.example.kitchen_appliances_android_java.Request.VerifyOTPRequest;
import com.example.kitchen_appliances_android_java.model.CartItem;
import com.example.kitchen_appliances_android_java.model.Customer;
import com.example.kitchen_appliances_android_java.model.Image;
import com.example.kitchen_appliances_android_java.model.Order;
import com.example.kitchen_appliances_android_java.model.Product;
import com.example.kitchen_appliances_android_java.model.Token;
import com.example.kitchen_appliances_android_java.util.JwtDecoder;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import javax.net.ssl.HttpsURLConnection;

public class ApiService {
    private HurlStack hurlStack;
    private Context context;

    public interface LoadCustomerCallback {
        void onCustomerLoaded(List<Customer> customers);

        void onError(Exception e);
    }

    public interface LoginCallback {
        void onLoginSuccess(String decodeString);

        void onError(Exception e);
    }

    public interface OrderCallback {
        void onSuccess(ArrayList<Order> orders);

        void onError(Exception e);
    }

    public ApiService(Context context) {
        this.context = context;
        hurlStack = new HurlStack() {
            @Override
            protected HttpURLConnection createConnection(URL url) throws IOException {
                HttpsURLConnection httpsURLConnection = (HttpsURLConnection) super.createConnection(url);
                try {
                    httpsURLConnection.setSSLSocketFactory(new TrustAllCertificatesSSLSocketFactory(null));
                    httpsURLConnection.setHostnameVerifier((hostname, session) -> true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return httpsURLConnection;
            }
        };
    }

    public void login(String email, String password, LoginCallback callback) {
        String url = "https://10.0.2.2:7178/gateway/account/login";
        JSONObject params = new JSONObject();
        try {
            params.put("email", email);
            params.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.POST, url, params, response -> {
                    Gson gson = new Gson();
                    Type responseType = new TypeToken<ApiResponse<Token>>() {
                    }.getType();
                    ApiResponse<Token> tokenResponse = gson.fromJson(String.valueOf(response), responseType);
                    if (tokenResponse == null) {
                        callback.onError(new Exception("Unable to login"));
                        return;
                    } else if (tokenResponse.getStatus() != 200) {
                        callback.onError(new Exception(tokenResponse.getMessage()));
                        return;
                    }
                    SharedPreferences sharedPreferences = context.getSharedPreferences("MySharedPref", MODE_PRIVATE);
                    SharedPreferences.Editor myEdit = sharedPreferences.edit();
                    myEdit.putString("email", email);
                    myEdit.putString("password", password);
                    myEdit.apply();
                    String decodeString = JwtDecoder.getInstance().decodeToken(tokenResponse.getData().getAccessToken());
                    callback.onLoginSuccess(decodeString);
                }, error -> {
                    error.printStackTrace();
                    callback.onError(error);
                });

        RequestQueue queue = Volley.newRequestQueue(context, hurlStack);
        queue.add(jsonObjectRequest);
    }

    public void loadCustomer(LoadCustomerCallback callback) {
        String url = "https://10.0.2.2:7178/gateway/customer";
        RequestQueue queue = Volley.newRequestQueue(context, hurlStack);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> {
                    Gson gson = new Gson();
                    Type responseType = new TypeToken<ApiResponse<ArrayList<Customer>>>() {
                    }.getType();
                    ApiResponse<ArrayList<Customer>> apiResponse = gson.fromJson(response, responseType);
                    if (apiResponse != null && apiResponse.getStatus() == 200) {
                        ArrayList<Customer> customers = apiResponse.getData();
                        callback.onCustomerLoaded(customers);
                    } else {
                        callback.onError(new Exception("Unable to fetch customer data"));
                    }
                }, error -> {
            error.printStackTrace();
            callback.onError(error);
        });

        queue.add(stringRequest);
    }

    public void loadCartDetails(int customerId, CartDetailsCallback callback) {
        RequestQueue queue = Volley.newRequestQueue(context, hurlStack);
        String url = "https://10.0.2.2:7178/gateway/cartdetail/" + customerId;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> {
                    Gson gson = new Gson();
                    ApiResponse<ArrayList<CartItem>> apiResponse = gson.fromJson(response, new TypeToken<ApiResponse<ArrayList<CartItem>>>() {
                    }.getType());
                    if (apiResponse != null && apiResponse.getStatus() == 200) {
                        callback.onSuccess(apiResponse.getData());
                    } else {
                        callback.onError(new Exception("Error: Unable to fetch product data"));
                    }
                }, error -> {
            error.printStackTrace();
            callback.onError(error);
        });
        queue.add(stringRequest);
    }

    public void loadProducts(ProductCallback callback) {
        RequestQueue queue = Volley.newRequestQueue(context, hurlStack);
//        String url = "https://10.0.2.2:7161/api/Product";
        String url = "https://10.0.2.2:7178/gateway/product";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> {
                    Gson gson = new Gson();
                    ApiResponse<ArrayList<Product>> apiResponse = gson.fromJson(response, new TypeToken<ApiResponse<ArrayList<Product>>>() {
                    }.getType());
                    if (apiResponse != null && apiResponse.getStatus() == 200) {
                        ArrayList<Product> products = apiResponse.getData();
                        AtomicInteger counter = new AtomicInteger(products.size());
                        for (Product product : products) {
                            fetchImagesForProduct(product, new ImageCallback() {
                                @Override
                                public void onSuccess(ArrayList<Image> images) {
                                    if (!images.isEmpty()) {
                                        product.setImage(images.get(0).getUrl());
                                    }
                                    if (counter.decrementAndGet() == 0) {
                                        callback.onSuccess(products);
                                    }
                                }

                                @Override
                                public void onError(Exception e) {
                                    e.printStackTrace();
                                    if (counter.decrementAndGet() == 0) {
                                        callback.onSuccess(products);
                                    }
                                }
                            });
                        }
                    } else {
                        callback.onError(new Exception("Error: Unable to fetch product data"));
                    }
                }, error -> {
            error.printStackTrace();
            callback.onError(error);
        });
        queue.add(stringRequest);
    }

    public void loadOrders(int customerId, OrderCallback callback) {
        RequestQueue queue = Volley.newRequestQueue(context, hurlStack);
//        String url = "https://10.0.2.2:7161/api/Order/get-order-by-customer/" + String.valueOf(customerId);
        String url = "https://10.0.2.2:7178/gateway/Order/get-order-by-customer/" + String.valueOf(customerId);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> {
                    Gson gson = new Gson();
                    ApiResponse<ArrayList<Order>> apiResponse = gson.fromJson(response, new TypeToken<ApiResponse<ArrayList<Order>>>() {
                    }.getType());
                    if (apiResponse != null && apiResponse.getStatus() == 200) {
                        callback.onSuccess(apiResponse.getData());
                    } else {
                        callback.onError(new Exception("Error: Unable to fetch order data"));
                    }
                }, error -> {
            error.printStackTrace();
            callback.onError(error);
        });
        queue.add(stringRequest);
    }

    public void fetchImagesForProduct(Product product, ImageCallback callback) {
        RequestQueue queue = Volley.newRequestQueue(context, hurlStack);
//        String url = "https://10.0.2.2:7161/api/Image/product/" + product.getId();
        String url = "https://10.0.2.2:7178/gateway/Image/product/" + product.getId();

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> {
                    Gson gson = new Gson();
                    ApiResponse<ArrayList<Image>> apiResponse = gson.fromJson(response, new TypeToken<ApiResponse<ArrayList<Image>>>() {
                    }.getType());
                    if (apiResponse != null && apiResponse.getStatus() == 200) {
                        ArrayList<Image> images = apiResponse.getData();
                        callback.onSuccess(images);
                    } else {
                        callback.onError(new Exception("Error: Unable to fetch image data"));
                    }
                }, error -> {
            error.printStackTrace();
            callback.onError(error);
        });
        queue.add(stringRequest);
    }

    public void loadProductById(int productId, SingleProductCallback callback) {
        RequestQueue queue = Volley.newRequestQueue(context, hurlStack);
//        String url = "https://10.0.2.2:7161/api/Product/" + productId;
        String url = "https://10.0.2.2:7178/gateway/Product/" + productId;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> {
                    Gson gson = new Gson();
                    ApiResponse<Product> apiResponse = gson.fromJson(response, new TypeToken<ApiResponse<Product>>() {
                    }.getType());
                    if (apiResponse != null && apiResponse.getStatus() == 200) {
                        Product product = apiResponse.getData();
                        fetchImagesForProduct(product, new ImageCallback() {
                            @Override
                            public void onSuccess(ArrayList<Image> images) {
                                if (!images.isEmpty()) {
                                    product.setImage(images.get(0).getUrl());
                                }
                                callback.onSuccess(product);
                            }

                            @Override
                            public void onError(Exception e) {
                                e.printStackTrace();
                            }
                        });
                    } else {
                        callback.onError(new Exception("Error: Unable to fetch product data"));
                    }
                }, error -> {
            error.printStackTrace();
            callback.onError(error);
        });
        queue.add(stringRequest);
    }
    public void signup(CreateCustomerRequest createCustomerRequest, SignUpCallback callback) {
        RequestQueue queue = Volley.newRequestQueue(context, hurlStack);
        String url = "https://10.0.2.2:7161/api/Customer";
        JSONObject params = new JSONObject();
        try {
            params.put("email", createCustomerRequest.getEmail());
            params.put("password", createCustomerRequest.getPassword());
            params.put("fullname", createCustomerRequest.getFullname());
            params.put("phoneNumber", createCustomerRequest.getPhoneNumber());
        }catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, params,
                response -> {
                    Gson gson = new Gson();
                    Type responseType = new TypeToken<ApiResponse<Boolean>>(){}.getType();
                    ApiResponse<Boolean> apiResponse = gson.fromJson(String.valueOf(response), responseType);
                    if(apiResponse.getStatus() != 200 && apiResponse.getData()) {
                        callback.onSignUpSuccess(false);
                        return;
                    }
                    callback.onSignUpSuccess(apiResponse.getData());
                }, error -> {
                    error.printStackTrace();
                    callback.onError(error);
                });
        queue.add(jsonObjectRequest);
    }

    public void resendOTP(ResendOTPRequest resendOTPRequest, ResendOTPRequestCallback callback) {
        RequestQueue queue = Volley.newRequestQueue(context, hurlStack);
        String url = "https://7161/api/Account/resend-otp";
        JSONObject params = new JSONObject();
        try {
            params.put("email", resendOTPRequest.getEmail());
            params.put("type", resendOTPRequest.getType());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.PUT, url, params,
                response -> {
                    Gson gson = new Gson();
                    Type responseType = new TypeToken<ApiResponse<Boolean>>() {
                    }.getType();
                    ApiResponse<Boolean> apiResponse = gson.fromJson(String.valueOf(response), responseType);
                    if (apiResponse.getStatus() != 200 && apiResponse.getData()) {

                        callback.onResendOTPSuccess(apiResponse.getData());
                        return;
                    }
                    callback.onResendOTPSuccess(false);
                }, error -> {
            error.printStackTrace();
            callback.onError(error);
        });
        queue.add(jsonObjectRequest);

    }
    public void verifyOTP(VerifyOTPRequest verifyOTPRequest, VerifyOTPRequestCallback callback) {
        RequestQueue queue = Volley.newRequestQueue(context, hurlStack);
        String url = "https://10.0.2.2:7161/api/Employee/active-account";
        JSONObject params = new JSONObject();
        try {
            params.put("email", verifyOTPRequest.getEmail());
            params.put("otp", verifyOTPRequest.getOtp());
        } catch (Exception e) {
            e.printStackTrace();
        }
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, params,
                response -> {
                    Gson gson = new Gson();
                    Type responseType = new TypeToken<ApiResponse<Boolean>>() {
                    }.getType();
                    ApiResponse<Boolean> apiResponse = gson.fromJson(String.valueOf(response), responseType);
                    if (apiResponse.getStatus() == 200 && apiResponse.getData()) {
                        callback.onVerifyOTPSuccess(apiResponse.getData());
                    }
                    else {
                        callback.onVerifyOTPSuccess(false);
                    }

                }, error -> {
            error.printStackTrace();
            callback.onError(error);
        });
        queue.add(jsonObjectRequest);
    }
    public interface VerifyOTPRequestCallback {
        void onVerifyOTPSuccess(Boolean result);

        void onError(Exception e);
    }
    public interface ResendOTPRequestCallback {
        void onResendOTPSuccess(Boolean result);

        void onError(Exception e);
    }
    public interface SignUpCallback {
        void onSignUpSuccess(Boolean result);

        void onError(Exception e);
    }

    public interface SingleProductCallback {
        void onSuccess(Product product);

        void onError(Exception e);
    }

    public interface ImageCallback {
        void onSuccess(ArrayList<Image> images);

        void onError(Exception e);
    }

    public interface ProductCallback {
        void onSuccess(ArrayList<Product> products);

        void onError(Exception e);
    }

    public interface CartDetailsCallback {
        void onSuccess(ArrayList<CartItem> cartDetails);

        void onError(Exception e);
    }
}
