package com.example.kitchen_appliances_android_java.fragment;

import static androidx.navigation.fragment.FragmentKt.findNavController;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.kitchen_appliances_android_java.R;
import com.example.kitchen_appliances_android_java.databinding.FragmentSignupBinding;

public class SignUpFragment extends Fragment {
    private FragmentSignupBinding binding;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentSignupBinding.inflate(inflater, container, false);
        // Inflate the layout for this fragment
        View view = binding.getRoot();
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setEvent();
    }

    private void setEvent() {
        binding.backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle back button click
                findNavController(SignUpFragment.this).popBackStack();

            }
        });
        binding.signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle sign up button click
                String name = binding.fullNameInput.getText().toString();
                String email = binding.emailInput.getText().toString();
                String phone = binding.phoneNumberInput.getText().toString();
                String address = binding.addressInput.getText().toString();
                String password = binding.passwordInput.getText().toString();
                String confirmPassword = binding.confirmPasswordInput.getText().toString();
                if (name.isEmpty()) {
                    binding.fullNameInput.setError("Tên không được bỏ trống");
                    return;
                }
                if (email.isEmpty()) {
                    binding.emailInput.setError("Email không được bỏ trống");
                    return;
                }
                if (phone.isEmpty()) {
                    binding.phoneNumberInput.setError("Số điện thoại không được bỏ trống");
                    return;
                }
                if (address.isEmpty()) {
                    binding.addressInput.setError("Địa chỉ không được bỏ trống");
                    return;
                }
                if (password.isEmpty()) {
                    binding.passwordInput.setError("Mật khẩu không được bỏ trống");
                    return;
                }
                if (confirmPassword.isEmpty()) {
                    binding.confirmPasswordInput.setError("Xác nhận mật khẩu không được bỏ trống");
                    return;
                }
                if (!password.equals(confirmPassword)) {
                    binding.confirmPasswordInput.setError("Mật khẩu không khớp");
                    return;
                }


                findNavController(SignUpFragment.this).navigate(R.id.action_signUpFragment_to_verifyOtpFragment);
            }
        });
    }
}
