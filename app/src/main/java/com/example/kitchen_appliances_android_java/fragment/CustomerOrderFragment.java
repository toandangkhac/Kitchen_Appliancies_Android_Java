package com.example.kitchen_appliances_android_java.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.kitchen_appliances_android_java.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CustomerOrderFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CustomerOrderFragment extends Fragment {


    public CustomerOrderFragment() {
        // Required empty public constructor
    }


    public static CustomerOrderFragment newInstance(String param1, String param2) {
        CustomerOrderFragment fragment = new CustomerOrderFragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_customer_order, container, false);
    }
}