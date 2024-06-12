package com.example.kitchen_appliances_android_java.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.example.kitchen_appliances_android_java.adapter.AccountsAdapter;
import com.example.kitchen_appliances_android_java.api.TrustAllCertificatesSSLSocketFactory;
import com.example.kitchen_appliances_android_java.databinding.FragmentAdminAccountBinding;
import com.example.kitchen_appliances_android_java.model.Account;
import com.example.kitchen_appliances_android_java.model.ApiResponse;
import com.example.kitchen_appliances_android_java.model.Order;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;

public class AdminAccountFragment extends Fragment {
    private FragmentAdminAccountBinding binding;
    private View root;
    private HurlStack hurlStack;
    private ArrayList<Account> accounts;

    private Spinner spn_account;
    private RecyclerView rv_account;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        rtHurlStack();
        binding = FragmentAdminAccountBinding.inflate(inflater, container, false);
        root = binding.getRoot();

        link();
        loadAccount();

        return root;
    }

    private void loadAccount() {
        RequestQueue queue = Volley.newRequestQueue(requireContext(), hurlStack);
        String url = "https://10.0.2.2:7161/api/Account";

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> {
                    Gson gson = new Gson();
                    ApiResponse<ArrayList<Account>> apiResponse = gson.fromJson(response, new TypeToken<ApiResponse<ArrayList<Account>>>() {}.getType());
                    if (apiResponse != null && apiResponse.getStatus() == 200) {
                        accounts = apiResponse.getData();
//                        Toast.makeText(getContext(), accounts.size() + "account loaded", Toast.LENGTH_SHORT).show();
                        updateView(accounts);
                    } else {
                        Toast.makeText(getContext(), apiResponse.getStatus() + ": " + apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }, error -> {
            error.printStackTrace();
            Toast.makeText(getContext(), "Error at order: " + error.getMessage(), Toast.LENGTH_SHORT).show();
        });
        queue.add(stringRequest);

    }

    private void updateView(ArrayList<Account> accounts) {
        AccountsAdapter adapter = new AccountsAdapter(accounts);
        rv_account.setAdapter(adapter);
        rv_account.setLayoutManager(new LinearLayoutManager(getContext()));
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

    private void link() {
        spn_account = root.findViewById(R.id.spn_account);
        rv_account = root.findViewById(R.id.rv_account);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}
