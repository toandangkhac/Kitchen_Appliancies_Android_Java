package com.example.kitchen_appliances_android_java.fragment;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.kitchen_appliances_android_java.R;
import com.example.kitchen_appliances_android_java.activity.LoginSignUpActivity;

public class AccountFragment extends Fragment {
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
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_account, container, false);

        // Set control
        btnLogout = view.findViewById(R.id.buttonLogout);

        // Set event
        btnLogout.setOnClickListener(v -> {
            // Logout
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