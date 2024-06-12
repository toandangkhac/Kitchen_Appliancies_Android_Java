package com.example.kitchen_appliances_android_java.fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.kitchen_appliances_android_java.R;
import com.example.kitchen_appliances_android_java.activity.LoginSignUpActivity;
import com.example.kitchen_appliances_android_java.databinding.FragmentAccountBinding;
import com.example.kitchen_appliances_android_java.databinding.FragmentAdminAccountBinding;

public class AccountFragment extends Fragment {
    private FragmentAccountBinding binding;

    Button btnLogout;

    public AccountFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentAccountBinding.inflate(inflater, container, false);
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_account, container, false);

        // Set control
        btnLogout = view.findViewById(R.id.buttonLogout);

        // Set event
        btnLogout.setOnClickListener(v -> {
            // Logout
            SharedPreferences sharedPreferences = getActivity().getSharedPreferences("MySharedPref", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.clear();
            editor.apply();
            Intent intent = new Intent(getActivity(), LoginSignUpActivity.class);
            startActivity(intent);
            getActivity().finish();
        });

        return view;
    }

    private void setEvent() {
        btnLogout.setOnClickListener(v -> {
            // Logout
            Intent intent = new Intent(getActivity(), LoginSignUpActivity.class);
            startActivity(intent);
            getActivity().finish();

        });
    }

    private void setControl() {
        btnLogout = getActivity().findViewById(R.id.buttonLogout);
    }
}