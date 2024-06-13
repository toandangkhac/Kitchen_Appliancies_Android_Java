package com.example.kitchen_appliances_android_java.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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
import com.example.kitchen_appliances_android_java.model.Category;
import com.example.kitchen_appliances_android_java.model.Product;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;

public class AddProductActivity extends AppCompatActivity {
    private HurlStack hurlStack;
    private Product product;
    private Category selectedCategory;
    EditText edt_product_name, edt_product_price, edt_description, edt_quantity;
    Button btnConfirm;
    Spinner spn_category;
    
    @Override
    @SuppressLint("MissingInflatedId")
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.product_add);
        rtHurlStack();
        link();
        loadCategory();

        btnConfirm.setOnClickListener(v -> {
            CreateNewProduct();
        });
    }

    private void CreateNewProduct() {
        String name = edt_product_name.getText().toString();
        String description = edt_description.getText().toString();
        int category = selectedCategory.getId();
        String price = edt_product_price.getText().toString();
        String quantity = edt_quantity.getText().toString();

        RequestQueue queue = Volley.newRequestQueue(this, hurlStack);
        String apiUrl = "https://10.0.2.2:7161/api/Product";
        JSONObject requestData = new JSONObject();
        try {
            requestData.put("name", name);
            requestData.put("description", description);
            requestData.put("categoryId", category);
            requestData.put("quantity", quantity);
            requestData.put("price", price);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        StringRequest stringRequest = new StringRequest(Request.Method.POST, apiUrl,
                response -> {
                    ApiResponse apiResponse = new Gson().fromJson(response, ApiResponse.class);
                    if(apiResponse.getStatus()==200) {
                        Toast.makeText(AddProductActivity.this, apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    } else{
                        Toast.makeText(this, apiResponse.getStatus() + ": " + apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                        Log.d("API Response", response);
                    }
                },
                error -> {
                    Log.e("API Error", error.toString());
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

        // Thêm yêu cầu vào hàng đợi
        queue.add(stringRequest);

    }


    private void loadCategory() {
        RequestQueue queue = Volley.newRequestQueue(AddProductActivity.this, hurlStack);
        String url = "https://10.0.2.2:7161/api/Category";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> {
                    Gson gson = new Gson();
                    ApiResponse<ArrayList<Category>> apiResponse = gson.fromJson(response, new TypeToken<ApiResponse<ArrayList<Category>>>() {}.getType());
                    if (apiResponse != null && apiResponse.getStatus() == 200) {
                        ArrayList<Category> categories = apiResponse.getData();
                        updateCategoriesUI(categories);
                        Toast.makeText(AddProductActivity.this, apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(AddProductActivity.this, apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }, error -> {
            error.printStackTrace();
            Toast.makeText(AddProductActivity.this , "Error at category: " + error.getMessage(), Toast.LENGTH_SHORT).show();
        });
        queue.add(stringRequest);
    }

    private void updateCategoriesUI(ArrayList<Category> categories) {
        ArrayAdapter<Category> adapter = new ArrayAdapter<>(AddProductActivity.this, android.R.layout.simple_spinner_item, categories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spn_category.setAdapter(adapter);

        spn_category.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedCategory = (Category) parent.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
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
        spn_category = findViewById(R.id.spn_category);
    }

}
