package com.example.kitchen_appliances_android_java;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.kitchen_appliances_android_java.api.TrustAllCertificatesSSLSocketFactory;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;

public class HomeFragment extends Fragment {
    private Button btnTest;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setEvent(view);
    }

    private void setEvent(View view) {
        btnTest = view.findViewById(R.id.btnTest);
        btnTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HurlStack hurlStack = new HurlStack() {
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

                RequestQueue queue = Volley.newRequestQueue(getContext(), hurlStack);
                String url = "https://10.0.2.2:7161/api/Account";
                StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                        response -> {
                            // Display the first 500 characters of the response string.
                            Toast.makeText(getContext(), "Response is: " + response.substring(0, 100), Toast.LENGTH_SHORT).show();
                        }, error -> {
                    error.printStackTrace();
                    Toast.makeText(getContext(), "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                });

                // Add the request to the RequestQueue.
                queue.add(stringRequest);

            }
        });
    }
}