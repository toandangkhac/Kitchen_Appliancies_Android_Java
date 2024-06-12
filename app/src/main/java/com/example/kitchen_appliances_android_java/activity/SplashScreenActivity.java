package com.example.kitchen_appliances_android_java.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.kitchen_appliances_android_java.R;
import com.example.kitchen_appliances_android_java.api.TrustAllCertificatesSSLSocketFactory;
import com.example.kitchen_appliances_android_java.api.ApiResponse;
import com.example.kitchen_appliances_android_java.model.Token;
import com.example.kitchen_appliances_android_java.util.JwtDecoder;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;

public class SplashScreenActivity extends AppCompatActivity {
    private HurlStack hurlStack;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_splash_screen);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
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
        // Delay for 1 second before loading user data
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                String url = "https://10.0.2.2:7178/gateway/account/login";
                // Check login status
                SharedPreferences sharedPreferences = getSharedPreferences("MySharedPref", MODE_PRIVATE);
                String email = sharedPreferences.getString("email", "");
                String password = sharedPreferences.getString("password", "");
                int customerId = sharedPreferences.getInt("customerId", 0);
                Log.d("SplashScreenActivity111", "CustomerId: " + customerId);
                JSONObject params = new JSONObject();
                try {
                    params.put("email", email);
                    params.put("password", password);
                } catch (JSONException e) {
//                    e.printStackTrace();
                    Log.e("SplashScreenActivity", "Error creating JSON params", e);
                    return;
                }
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                        (Request.Method.POST, url, params, response -> {
//                                Intent intent = new Intent(SplashScreenActivity.this, MainActivity.class);
//                                startActivity(intent);
                            Gson gson = new Gson();
                            Type responseType = new TypeToken<ApiResponse<Token>>(){}.getType();
                            ApiResponse<Token> token = gson.fromJson(String.valueOf(response), responseType);

                            String decodeString = JwtDecoder.getInstance().decodeToken(token.getData().getAccessToken());


// ...

                            if(decodeString.contains("Khách hàng")){
                                Log.d("SplashScreenActivity", "Decoded token: " + "Khách hàng");
                            } else if (decodeString.contains("Nhân viên")) {
                                Log.d("SplashScreenActivity", "Decoded token: " + "Nhân viên");
                            } else {
                                Log.d("SplashScreenActivity", "Decoded token: " + "Admin");

                            }
                            Log.d("SplashScreenActivity", "Decoded token: " +decodeString);
                            startActivity(new Intent(SplashScreenActivity.this, MainActivity.class));
                            finish();

                        }, error ->  {
                            Log.e("SplashScreenActivity", "Login Error", error);
//                            Intent intent = new Intent(SplashScreenActivity.this, MainActivity.class);
//                            startActivity(intent);
                            startActivity(new Intent(SplashScreenActivity.this, LoginSignUpActivity.class));
                            finish();
                        });


                RequestQueue queue = Volley.newRequestQueue(getApplicationContext(), hurlStack);
                queue.add(jsonObjectRequest);


            }
        }, 2000); // Delay for 1 second
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull String name, @NonNull Context context, @NonNull AttributeSet attrs) {
        return super.onCreateView(name, context, attrs);
    }
}