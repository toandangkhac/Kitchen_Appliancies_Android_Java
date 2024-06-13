package com.example.kitchen_appliances_android_java.fragment;

import static androidx.navigation.fragment.FragmentKt.findNavController;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.kitchen_appliances_android_java.R;
import com.example.kitchen_appliances_android_java.Request.CreateCustomerRequest;
import com.example.kitchen_appliances_android_java.Request.ResendOTPRequest;
import com.example.kitchen_appliances_android_java.Request.VerifyOTPRequest;
import com.example.kitchen_appliances_android_java.api.ApiService;

public class VerifyOtpFragment extends Fragment {
    TextView countdownTimer;
    Button resendOtpButton, verifyOtpButton;
    CountDownTimer countDownTimer;
    ApiService apiService;
    EditText number1ofOtp, number2ofOtp, number3ofOtp, number4ofOtp, number5ofOtp, number6ofOtp;
    String email;
    public VerifyOtpFragment() {
        // Required empty public constructor
    }

    public static VerifyOtpFragment newInstance() {
        VerifyOtpFragment fragment = new VerifyOtpFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        apiService = new ApiService(getContext());

        super.onCreate(savedInstanceState);


    }

    private void setEvent() {
        resendOtpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Resend OTP
                ResendOTPRequest resendOTPRequest = new ResendOTPRequest(email,"REGISTER_OTP");
                apiService.resendOTP(resendOTPRequest , new ApiService.ResendOTPRequestCallback() {

                    @Override
                    public void onResendOTPSuccess(Boolean result) {
                        if (result) {
                            countDownTimer.start();
                            verifyOtpButton.setVisibility(View.GONE);
                        }
                        else {
                            Toast.makeText(getContext(), "Resend OTP failed", Toast.LENGTH_SHORT).show();

                        }
                    }

                    @Override
                    public void onError(Exception e) {
                        Toast.makeText(getContext(), "Resend OTP failed", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        verifyOtpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Verify OTP

                String otp = number1ofOtp.getText().toString() + number2ofOtp.getText().toString() + number3ofOtp.getText().toString() + number4ofOtp.getText().toString() + number5ofOtp.getText().toString() + number6ofOtp.getText().toString();
                VerifyOTPRequest verifyOTPRequest = new VerifyOTPRequest(email, otp);
                apiService.verifyOTP(verifyOTPRequest, new ApiService.VerifyOTPRequestCallback() {
                    @Override
                    public void onVerifyOTPSuccess(Boolean result) {
                        if (result) {
                            Toast.makeText(getContext(), "Xác thực OTP thành công", Toast.LENGTH_SHORT).show();
                            findNavController(VerifyOtpFragment.this).popBackStack(R.id.loginFragment, false);


                        }
                        else {
                            Toast.makeText(getContext(), "Xác thực OTP thất bại", Toast.LENGTH_SHORT).show();

                        }
                    }

                    @Override
                    public void onError(Exception e) {
                        Log.d("VerifyOtpFragment111", "onError: " + e.getMessage());
                        Toast.makeText(getContext(), "Looi server", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });
        number1ofOtp.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 1) {
                    number2ofOtp.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        number2ofOtp.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 1) {
                    number3ofOtp.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        number3ofOtp.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 1) {
                    number4ofOtp.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        number4ofOtp.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 1) {
                    number5ofOtp.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        number5ofOtp.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 1) {
                    number6ofOtp.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void setControl() {
        countdownTimer = getView().findViewById(R.id.countdownTimer);
        resendOtpButton = getView().findViewById(R.id.resendOtpButton);
        verifyOtpButton = getView().findViewById(R.id.verifyOtpButton);
        number1ofOtp = getView().findViewById(R.id.number1ofOtp);
        number2ofOtp = getView().findViewById(R.id.number2ofOtp);
        number3ofOtp = getView().findViewById(R.id.number3ofOtp);
        number4ofOtp = getView().findViewById(R.id.number4ofOtp);
        number5ofOtp = getView().findViewById(R.id.number5ofOtp);
        number6ofOtp = getView().findViewById(R.id.number6ofOtp);


        countDownTimer = new CountDownTimer(300000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                long minutes = (millisUntilFinished / 1000) / 60;
                long seconds = (millisUntilFinished / 1000) % 60;
                countdownTimer.setText(minutes + ":" + seconds);
            }

            @Override
            public void onFinish() {
                resendOtpButton.setVisibility(View.VISIBLE);
            }
        };

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_verify_otp, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            email = args.getString("email");
        }
        setControl();
        setEvent();
//        countDownTimer.start();
    }
}