package com.example.kitchen_appliances_android_java.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.kitchen_appliances_android_java.R;
import com.example.kitchen_appliances_android_java.adapter.ProductAdapter;
import com.example.kitchen_appliances_android_java.api.TrustAllCertificatesSSLSocketFactory;
import com.example.kitchen_appliances_android_java.api.ApiResponse;
import com.example.kitchen_appliances_android_java.model.Category;
import com.example.kitchen_appliances_android_java.model.Image;
import com.example.kitchen_appliances_android_java.model.Product;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;

public class HomeFragment extends Fragment {
    private HurlStack hurlStack;
    private ArrayList<Product> products;
    private Map<Integer,String> images;
    private ArrayList<Image> tempList;
    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
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
        images = new HashMap<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loadProducts();
        loadCategories();
        setSearchView();
    }




    private void setSearchView() {
        SearchView searchView = getView().findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchProductsByName(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (newText.isEmpty()) {
                    updateUI(products); // Show all products when search text is cleared
                } else {
                    searchProductsByName(newText);
                }
                return true;
            }
        });
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
                        Log.d("HomeFragment123", "Image URL: " + tempList.get(0).getUrl());
                        product.setImage(tempList.get(0).getUrl());
                    } else {
//                        Toast.makeText(getContext(), "Error: Unable to fetch image data", Toast.LENGTH_SHORT).show();
                        images.put(product.getId(), "https://via.placeholder.com/150");
                    }
                }, error -> {
            error.printStackTrace();
            Log.d("HomeFragment", "Error: " + error.getMessage());
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
                        Toast.makeText(getContext(), "Categories loaded", Toast.LENGTH_SHORT).show();
                        updateCategoriesUI(categories);
                    } else {
                        Toast.makeText(getContext(), "Error: Unable to fetch category data", Toast.LENGTH_SHORT).show();
                    }
//                    Type type = new TypeToken<ApiResponse<ArrayList<Category>>>() {}.getType();
//                    ApiResponse<ArrayList<Category>> apiResponse = gson.fromJson(response.toString(), type);
//                    ArrayList<Category> categories = apiResponse.getData();
//                    Toast.makeText(getContext(), "Categories loaded", Toast.LENGTH_SHORT).show();
//                    updateCategoriesUI(categories);
                }, error -> {
            error.printStackTrace();
            Toast.makeText(getContext(), "Error at category: " + error.getMessage(), Toast.LENGTH_SHORT).show();
        });
        queue.add(stringRequest);
    }


    private void loadProducts() {
        RequestQueue queue = Volley.newRequestQueue(getContext(), hurlStack);
        String url = "https://10.0.2.2:7161/api/Product";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> {
                    Gson gson = new Gson();
//                    Type productListType = new TypeToken<ArrayList<Product>>() {
//                    }.getType();
//                    products = gson.fromJson(response, productListType);
//                    updateUI(products);
                    ApiResponse<ArrayList<Product>> apiResponse = gson.fromJson(response, new TypeToken<ApiResponse<ArrayList<Product>>>() {}.getType());
                    if (apiResponse != null && apiResponse.getStatus() == 200) {
                        products = apiResponse.getData();
                        for(Product product: products){
                            fetchImagesForProduct(product);
                        }

                    } else {
                        Toast.makeText(getContext(), "Error: Unable to fetch product data", Toast.LENGTH_SHORT).show();
                    }
                }, error -> {
            error.printStackTrace();
            Toast.makeText(getContext(), "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
        });
        queue.add(stringRequest);
    }

    private void updateCategoriesUI(ArrayList<Category> categories) {
        Spinner spinnerCategories = getView().findViewById(R.id.spinnerCategory);
        ArrayAdapter<Category> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, categories);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategories.setAdapter(adapter);

        spinnerCategories.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Category selectedCategory = (Category) parent.getItemAtPosition(position);
                filterProductsByCategory(selectedCategory.getId());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // Do nothing
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
        RecyclerView rvProducts = getView().findViewById(R.id.rvProducts);
        ProductAdapter adapter = new ProductAdapter(filteredProducts);
        rvProducts.setAdapter(adapter);
        rvProducts.setLayoutManager(new LinearLayoutManager(getContext()));
    }
    private void searchProductsByName(String name) {
        ArrayList<Product> searchedProducts = new ArrayList<>();
        for (Product product : products) {
            if (product.getName().toLowerCase().contains(name.toLowerCase())) {
                searchedProducts.add(product);
            }
        }
        updateUI(searchedProducts);
    }
}