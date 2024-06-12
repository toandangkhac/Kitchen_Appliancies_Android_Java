package com.example.kitchen_appliances_android_java.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.kitchen_appliances_android_java.R;
import com.example.kitchen_appliances_android_java.api.ApiResponse;
import com.example.kitchen_appliances_android_java.api.TrustAllCertificatesSSLSocketFactory;

import com.example.kitchen_appliances_android_java.model.Product;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;

public class EditProductActivity extends AppCompatActivity {
    private HurlStack hurlStack;
    private Product product;

    EditText edt_product_name, edt_product_price, edt_description, edt_quantity;
    Button btnConfirm;

    @Override
    @SuppressLint("MissingInflatedId")
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.product_edit);

        rtHurlStack();
        link();

        product = (Product) getIntent().getSerializableExtra("product");

        edt_product_name.setText(product.getName());
        edt_product_price.setText(String.valueOf(product.getPrice()));
        edt_description.setText(product.getDescription());
        edt_quantity.setText(String.valueOf(product.getQuantity()));

        btnConfirm.setOnClickListener(v -> {
            confirm();
        });
    }

    private void confirm() {
        String name = edt_product_name.getText().toString();
        double price = Double.parseDouble(edt_product_price.getText().toString());
        String description = edt_description.getText().toString();
        int quantity = Integer.parseInt(edt_quantity.getText().toString());

        RequestQueue queue = Volley.newRequestQueue(this, hurlStack);
        String url = "https://10.0.2.2:7161/api/Product/" + product.getId();

        JSONObject requestData = new JSONObject();
        try {
            requestData.put("name", name);
            requestData.put("description", description);
            requestData.put("quantity", quantity);
            requestData.put("price", price);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        StringRequest stringRequest = new StringRequest(Request.Method.PUT, url,
            response -> {
                ApiResponse apiResponse = new Gson().fromJson(response, ApiResponse.class);
                if (apiResponse.getStatus()==200) {
                    Toast.makeText(this, apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                } else
                    Toast.makeText(this,apiResponse.getStatus()+ ": " +apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
            }, error -> {
                Log.e("API Error", error.toString());
                error.printStackTrace();
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

    private void rtHurlStack() {
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

    public void link(){
        edt_product_name = findViewById(R.id.edt_product_name);
        edt_product_price = findViewById(R.id.edt_product_price);
        edt_description = findViewById(R.id.edt_description);
        edt_quantity = findViewById(R.id.edt_quantity);
        btnConfirm = findViewById(R.id.btnConfirm);
    }

}
