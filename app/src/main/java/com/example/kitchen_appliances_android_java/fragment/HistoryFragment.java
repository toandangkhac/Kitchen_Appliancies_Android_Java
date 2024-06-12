package com.example.kitchen_appliances_android_java.fragment;

import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.kitchen_appliances_android_java.R;
import com.example.kitchen_appliances_android_java.adapter.OrderAdapter;
import com.example.kitchen_appliances_android_java.api.ApiResponse;
import com.example.kitchen_appliances_android_java.api.ApiService;
import com.example.kitchen_appliances_android_java.model.Order;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;


public class HistoryFragment extends Fragment {
    private RecyclerView historyRecyclerView;

    private ArrayList<Order> orders;
    private ApiService apiService;

    public HistoryFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        apiService = new ApiService(getContext());

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_history, container, false);

        historyRecyclerView = view.findViewById(R.id.historyRecyclerView);
        loadOrders();

        return view;
    }

    private void loadOrders() {

        SharedPreferences sharedPreferences = requireActivity().getSharedPreferences("MySharedPref", getContext().MODE_PRIVATE);
        int customerId = sharedPreferences.getInt("customerId", 0);
        Log.d("customerId", String.valueOf(customerId));

        ApiService apiService = new ApiService(getContext());


        String url = "https://10.0.2.2:7161/api/Order/get-order-by-customer/" + String.valueOf(customerId);
        apiService.loadOrders(customerId, new ApiService.OrderCallback() {
            @Override
            public void onSuccess(ArrayList<Order> orders1) {
//                Log.d("Orders", orders.toString());
                orders = orders1;
                OrderAdapter orderAdapter = new OrderAdapter(orders);
                historyRecyclerView.setAdapter(orderAdapter);
                historyRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            }

            @Override
            public void onError(Exception e) {
                Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("Error111", e.getMessage());
            }
        });


    }
}