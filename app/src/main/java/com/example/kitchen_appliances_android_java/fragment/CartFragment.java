package com.example.kitchen_appliances_android_java.fragment;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kitchen_appliances_android_java.R;
import com.example.kitchen_appliances_android_java.adapter.CartItemAdapter;
import com.example.kitchen_appliances_android_java.api.ApiService;
import com.example.kitchen_appliances_android_java.model.CartItem;
import com.example.kitchen_appliances_android_java.model.Product;

import java.util.ArrayList;


public class CartFragment extends Fragment {
    private RecyclerView recyclerView;
    private CartItemAdapter adapter;
    private ArrayList<CartItem> listCartItem;
    private ApiService apiService;
    private TextView tvTotalPrice;
    double totalPrice = 0;
    private Button btnCheckout;

    public CartFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        apiService = new ApiService(getContext());
        listCartItem = new ArrayList<>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_cart, container, false);
        recyclerView = view.findViewById(R.id.cartRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        tvTotalPrice = view.findViewById(R.id.tvTotalPrice);
        loadCartDetails();
        btnCheckout = view.findViewById(R.id.checkoutButton);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        btnCheckout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences sharedPreferences = getContext().getSharedPreferences("MySharedPref", getContext().MODE_PRIVATE);
                int customerId = sharedPreferences.getInt("customerId", 0);

            }
        });
    }

    public void loadCartDetails() {
        SharedPreferences sharedPreferences = getContext().getSharedPreferences("MySharedPref", getContext().MODE_PRIVATE);
        int customerId = sharedPreferences.getInt("customerId", 0);

        apiService.loadCartDetails(customerId, new ApiService.CartDetailsCallback() {
            @Override
            public void onSuccess(ArrayList<CartItem> listCartItem1) {
                listCartItem = listCartItem1;
                adapter = new CartItemAdapter(listCartItem, getContext(), new CartItemAdapter.OnQuantityChangeListener() {
                    @Override
                    public void onQuantityChange(double priceDifference) {
                        totalPrice += priceDifference;
                        tvTotalPrice.setText(String.valueOf( totalPrice));
                    }
                });
                recyclerView.setAdapter(adapter);
                for (CartItem cartItem : listCartItem1) {
                    apiService.loadProductById(cartItem.getProductId(), new ApiService.SingleProductCallback() {
                        @SuppressLint("NotifyDataSetChanged")
                        @Override
                        public void onSuccess(Product product) {
                            cartItem.setPrice(cartItem.getQuantity() * product.getPrice());
                            totalPrice += cartItem.getPrice();
                            adapter.notifyDataSetChanged();
                            tvTotalPrice.setText(String.valueOf(totalPrice));
                        }

                        @Override
                        public void onError(Exception e) {
                            e.printStackTrace();
                            Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }

            }

            @Override
            public void onError(Exception e) {
                e.printStackTrace();
                Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}