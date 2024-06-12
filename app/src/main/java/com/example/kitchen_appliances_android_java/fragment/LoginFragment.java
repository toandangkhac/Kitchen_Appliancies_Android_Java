package com.example.kitchen_appliances_android_java.fragment;

import static android.content.Context.MODE_PRIVATE;
import static androidx.navigation.fragment.FragmentKt.findNavController;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.kitchen_appliances_android_java.R;
import com.example.kitchen_appliances_android_java.activity.AdminMainActivity;
import com.example.kitchen_appliances_android_java.activity.MainActivity;
import com.example.kitchen_appliances_android_java.activity.ProductDetail;
import com.example.kitchen_appliances_android_java.api.ApiService;
import com.example.kitchen_appliances_android_java.databinding.FragmentLoginBinding;

import java.util.List;

import com.example.kitchen_appliances_android_java.model.Customer;

public class LoginFragment extends Fragment {
    private FragmentLoginBinding binding;
    ApiService apiService;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentLoginBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        apiService = new ApiService(getContext());
        setEvent();


    }


    public void setEvent() {
        binding.btnLogin.setOnClickListener(v -> {
            String email = binding.edtEmail.getText().toString();
            String password = binding.edtPassword.getText().toString();
            apiService.login(email, password, new ApiService.LoginCallback() {
                @Override
                public void onLoginSuccess(String decodeString) {
                    if (decodeString.contains("Khách hàng")) {
                        Intent intent = new Intent(getActivity(), MainActivity.class);
                        getActivity().finish();
                        startActivity(intent);
                        apiService.loadCustomer(new ApiService.LoadCustomerCallback() {
                            @Override
                            public void onCustomerLoaded(List<Customer> customers) {
                                for (Customer c : customers) {
                                    if (c.getEmail().equals(email)) {
                                        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("MySharedPref", MODE_PRIVATE);
                                        SharedPreferences.Editor myEdit = sharedPreferences.edit();
                                        myEdit.putInt("customerId", c.getId());
                                        myEdit.commit();
                                        return;
                                    }
                                }
                            }

                            @Override
                            public void onError(Exception e) {
                                // Handle the error
                            }
                        });

                    } else if (decodeString.contains("Quản trị viên")) {
                        Intent intent = new Intent(getActivity(), AdminMainActivity.class);
                        getActivity().finish();
                        startActivity(intent);
                    } else {
                        Intent intent = new Intent(getActivity(), AdminMainActivity.class);
                        getActivity().finish();
                        startActivity(intent);
                    }
                    SharedPreferences sharedPreferences = getActivity().getSharedPreferences("MySharedPref", MODE_PRIVATE);
                    SharedPreferences.Editor myEdit = sharedPreferences.edit();
                    myEdit.putString("email", email);
                    myEdit.putString("password", password);
                    myEdit.apply();
                }

                @Override
                public void onError(Exception e) {
                    Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        });
        binding.btnRegister.setOnClickListener(v -> {
            findNavController(LoginFragment.this).navigate(R.id.action_loginFragment_to_signUpFragment, null);
        });
    }


}
