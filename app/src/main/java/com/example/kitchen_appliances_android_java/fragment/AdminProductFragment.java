package com.example.kitchen_appliances_android_java.fragment;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.kitchen_appliances_android_java.R;
import com.example.kitchen_appliances_android_java.activity.AddProductActivity;
import com.example.kitchen_appliances_android_java.adapter.AdminProductAdapter;
import com.example.kitchen_appliances_android_java.api.ApiResponse;
import com.example.kitchen_appliances_android_java.api.TrustAllCertificatesSSLSocketFactory;
import com.example.kitchen_appliances_android_java.databinding.FragmentAdminProductBinding;

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

public class AdminProductFragment extends Fragment {
    private FragmentAdminProductBinding binding;
    private View root;
    private HurlStack hurlStack;
    private ArrayList<Product> products;

    private Button btnProduct, btnCategory, btn_add_product, btn_add_category;
    private Spinner spinner_category;
    private RecyclerView rv_item;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        rtHurlStack();
        binding = FragmentAdminProductBinding.inflate(inflater, container, false);
        root = binding.getRoot();

        link();
        loadProducts();
        loadCategories();

        setEvent();
        return root;
    }

    private void setEvent() {
        btn_add_product.setOnClickListener(v -> {
            Intent intent = new Intent(getContext(), AddProductActivity.class);
            v.getContext().startActivity(intent);
        });

        btn_add_category.setOnClickListener(v ->  {
            AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
            builder.setTitle("Thêm danh mục");

            // Tạo EditText để nhập văn bản
            final EditText editText = new EditText(getContext());
            builder.setView(editText);

            // Thêm nút "Xác nhận"
            builder.setPositiveButton("Xác nhận", (dialog, which) -> {
                String categoryName = editText.getText().toString();
//                Toast.makeText(getContext(), categoryName, Toast.LENGTH_LONG).show();
                RequestQueue queue = Volley.newRequestQueue(requireContext(), hurlStack);
                String url = "https://10.0.2.2:7161/api/Category";

                JSONObject requestData = new JSONObject();
                try {
                    requestData.put("name", categoryName);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                    response -> {
                        ApiResponse apiResponse = new Gson().fromJson(response, ApiResponse.class);
                        if(apiResponse.getStatus()==200) {
                            Toast.makeText(getContext(), apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getContext(), apiResponse.getStatus() + ": " + apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }, error -> {
                        Log.e("API Error", error.toString());
                })  {
                    @Override
                    public byte[] getBody() {
                        return requestData.toString().getBytes();
                    }

                    @Override
                    public String getBodyContentType() {
                        return "application/json";
                    }
                };
            });

            builder.setNegativeButton("Hủy", (dialog, which) -> {
                //nothing
            });

            builder.show();
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

    private void loadProducts() {
        RequestQueue queue = Volley.newRequestQueue(requireContext(), hurlStack);
        String url = "https://10.0.2.2:7161/api/Product";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
            response -> {
                Gson gson = new Gson();

                ApiResponse<ArrayList<Product>> apiResponse = gson.fromJson(response, new TypeToken<ApiResponse<ArrayList<Product>>>() {}.getType());
                if (apiResponse != null && apiResponse.getStatus() == 200) {
//                        Toast.makeText(getContext(), apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    products = apiResponse.getData();
                    updateUI(products);
                } else {
                    Toast.makeText(getContext(), apiResponse.getStatus() + ": " + apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }, error -> {
                error.printStackTrace();
                Toast.makeText(getContext(), "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            });
        queue.add(stringRequest);
    }

    private void loadCategories() {
        RequestQueue queue = Volley.newRequestQueue(getContext(), hurlStack);
        String url = "https://10.0.2.2:7161/api/Category";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> {
                    Gson gson = new Gson();
                    ApiResponse<ArrayList<Category>> apiResponse = gson.fromJson(response, new TypeToken<ApiResponse<ArrayList<Category>>>() {}.getType());
                    if (apiResponse != null && apiResponse.getStatus() == 200) {
                        ArrayList<Category> categories = apiResponse.getData();
                        updateCategoriesUI(categories);
                    } else {
                        Toast.makeText(getContext(), "Error: Unable to fetch category data", Toast.LENGTH_SHORT).show();
                    }
                }, error -> {
            error.printStackTrace();
            Toast.makeText(getContext(), "Error at category: " + error.getMessage(), Toast.LENGTH_SHORT).show();
        });
        queue.add(stringRequest);
    }

    private void updateCategoriesUI(ArrayList<Category> categories) {
        ArrayAdapter<Category> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, categories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner_category.setAdapter(adapter);

        spinner_category.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Category selectedCategory = (Category) parent.getItemAtPosition(position);
                filterProductsByCategory(selectedCategory.getId());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void filterProductsByCategory(int categoryId) {
        ArrayList<Product> filteredProducts = new ArrayList<>();
        if(products  == null){
            Toast.makeText(getContext(), "Products are null", Toast.LENGTH_SHORT).show();
        } else{
            for (Product product : products) {
                if (product.getCategoryId() == categoryId) {
                    filteredProducts.add(product);
                }
            }
            updateUI(filteredProducts);
        }

    }

    private void updateUI(ArrayList<Product> filteredProducts) {
//        Toast.makeText(getContext(), "prd: " + filteredProducts.size(), Toast.LENGTH_SHORT).show();
        AdminProductAdapter adapter = new AdminProductAdapter(filteredProducts);
        rv_item.setAdapter(adapter);
        rv_item.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    public void link() {
        btn_add_product = root.findViewById(R.id.btn_add_product);
        btn_add_category = root.findViewById(R.id.btn_add_category);
        spinner_category = root.findViewById(R.id.spn_category);
        rv_item = root.findViewById(R.id.rv_product_category);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
