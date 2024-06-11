package com.example.kitchen_appliances_android_java.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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
import com.example.kitchen_appliances_android_java.adapter.AdminProductAdapter;
import com.example.kitchen_appliances_android_java.adapter.OrderAdapter;
import com.example.kitchen_appliances_android_java.api.TrustAllCertificatesSSLSocketFactory;
import com.example.kitchen_appliances_android_java.databinding.FragmentAdminOrderBinding;
import com.example.kitchen_appliances_android_java.model.ApiResponse;
import com.example.kitchen_appliances_android_java.model.Category;
import com.example.kitchen_appliances_android_java.model.Order;
import com.example.kitchen_appliances_android_java.model.Product;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;

public class AdminOrderFragment extends Fragment {
    private FragmentAdminOrderBinding binding;
    private View root;
    private HurlStack hurlStack;
    private ArrayList<Order> orders;
    
    private RecyclerView rv_order;
    private Spinner spn_order;
    
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        rtHurlStack();
        binding = FragmentAdminOrderBinding.inflate(inflater, container, false);
        root = binding.getRoot();

        link();
        loadSpinner();

        return root;
    }

    private void UpdateView(ArrayList<Order> filteredOrders) {
        OrderAdapter adapter = new OrderAdapter(filteredOrders);
        rv_order.setAdapter(adapter);
        rv_order.setLayoutManager(new LinearLayoutManager(getContext()));
    }

    private void loadSpinner() {
        List<String> spinnerValues = new ArrayList<>();
        spinnerValues.add("Chưa xác nhận");
        spinnerValues.add("Đã xác nhận");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, spinnerValues);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spn_order.setAdapter(adapter);

        spn_order.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (spn_order.getSelectedItem().toString().equals("Chưa xác nhận")) {
                    loadOrders(1);
                } else if (spn_order.getSelectedItem().toString().equals("Đã xác nhận")) {
                    loadOrders(2);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void loadOrders(int stt) {
        RequestQueue queue = Volley.newRequestQueue( requireContext(), hurlStack);
        String url = "";
        if(stt == 1){
            url = "https://10.0.2.2:7161/api/Order/list-order-not-confirm";
        } else if(stt == 2) {
            url = "https://10.0.2.2:7161/api/Order/list-order-confirm";
        }


        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
            response -> {
//            Toast.makeText(getContext(), "Response is: " + response, Toast.LENGTH_LONG).show();
                Gson gson = new Gson();
                ApiResponse<ArrayList<Order>> apiResponse = gson.fromJson(response, new TypeToken<ApiResponse<ArrayList<Order>>>() {}.getType());
                if (apiResponse != null && apiResponse.getStatus() == 200) {
                    orders = apiResponse.getData();
//                    Toast.makeText(getContext(), "orders:" + orders.size() + " orders loaded", Toast.LENGTH_SHORT).show();
                    UpdateView(orders);
                } else {
                    Toast.makeText(getContext(), apiResponse.getStatus() + ": " + apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }, error -> {
                error.printStackTrace();
                Toast.makeText(getContext(), "Error at order: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            });
        queue.add(stringRequest);
    }




    private void link() {
        rv_order = root.findViewById(R.id.rv_order);
        spn_order = root.findViewById(R.id.spn_order);
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
