package com.example.kitchen_appliances_android_java.fragment;

import static android.content.Context.MODE_PRIVATE;
import static androidx.navigation.fragment.FragmentKt.findNavController;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.example.kitchen_appliances_android_java.R;
import com.example.kitchen_appliances_android_java.activity.MainActivity;
import com.example.kitchen_appliances_android_java.activity.ProductDetail;
import com.example.kitchen_appliances_android_java.api.TrustAllCertificatesSSLSocketFactory;
import com.example.kitchen_appliances_android_java.databinding.FragmentLoginBinding;

import java.io.IOException;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.kitchen_appliances_android_java.model.ApiResponse;
import com.example.kitchen_appliances_android_java.model.Category;
import com.example.kitchen_appliances_android_java.model.Token;
import com.example.kitchen_appliances_android_java.model.TokenResponse;
import com.example.kitchen_appliances_android_java.util.JwtDecoder;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

public class LoginFragment extends Fragment {
    private FragmentLoginBinding binding;
    private HurlStack hurlStack;
    Button btnLogin, btnRegister;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentLoginBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setEvent();


    }


    public void setEvent() {
        binding.btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override

            public void onClick(View v) {
                String url = "https://10.0.2.2:7178/gateway/account/login";

//                String url = "https://10.0.2.2:7161/api/Account/login";
                String email = binding.edtEmail.getText().toString();
                String password = binding.edtPassword.getText().toString();
                JSONObject params = new JSONObject();
                try {
                    params.put("email", email);
                    params.put("password", password);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                        (Request.Method.POST, url, params, new Response.Listener<JSONObject>() {

                            @Override
                            public void onResponse(JSONObject response) {
                                // Handle response
                                Gson gson = new Gson();
                                Type responseType = new TypeToken<ApiResponse<Token>>(){}.getType();
                                ApiResponse<Token> tokenResponse = gson.fromJson(String.valueOf(response), responseType);

                                // Get the Data object from the TokenResponse
//                                Token data = tokenResponse.getData();
                                SharedPreferences sharedPreferences = getActivity().getSharedPreferences("MySharedPref", MODE_PRIVATE);
                                SharedPreferences.Editor myEdit = sharedPreferences.edit();
                                myEdit.putString("email", email);
                                myEdit.putString("password", password);
                                // Đăng xuất phải remove email, password, token.
//                                myEdit.putString("accessToken", data.getAccessToken());
//                                myEdit.putString("refreshToken", data.getRefreshToken());
                                myEdit.commit();


                                String decodeString = JwtDecoder.getInstance().decodeToken(tokenResponse.getData().getAccessToken());
                                if(decodeString.contains("Khách hàng")){
                                    Log.d("SplashScreenActivity", "Decoded token: " + "Khách hàng");
                                    Intent intent = new Intent(getActivity(), MainActivity.class);
                                    getActivity().finish();
                                    startActivity(intent);
                                } else if (decodeString.contains("Quản trị viên")) {
                                    Log.d("SplashScreenActivity", "Decoded token: " + "Quản trị viên");
                                    Intent intent = new Intent(getActivity(), ProductDetail.class);
                                    getActivity().finish();
                                    startActivity(intent);
                                } else {
                                    Log.d("SplashScreenActivity", "Decoded token: " + "Nhân viên");
                                    Intent intent = new Intent(getActivity(), ProductDetail.class);
                                    getActivity().finish();
                                    startActivity(intent);
                                }
                                Toast.makeText(getContext(), "Login successful:"  , Toast.LENGTH_SHORT).show();
                                Log.d("Login", "Login successful: " + decodeString);
//                                Intent intent = new Intent(getActivity(), MainActivity.class);
//                                getActivity().finish();
//                                startActivity(intent);
                            }
                        }, new Response.ErrorListener() {

                            @Override
                            public void onErrorResponse(VolleyError error) {
                                // Handle error
                                Toast.makeText(getContext(), "Error: " + error.toString(), Toast.LENGTH_SHORT).show();
                            }
                        });

                RequestQueue queue = Volley.newRequestQueue(getContext(), hurlStack);
                queue.add(jsonObjectRequest);
            }
        });
        binding.btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle register button click
                findNavController(LoginFragment.this).navigate(R.id.action_loginFragment_to_signUpFragment, null);
            }
        });
    }
}
