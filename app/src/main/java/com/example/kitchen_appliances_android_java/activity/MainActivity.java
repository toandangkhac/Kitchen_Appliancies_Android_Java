package com.example.kitchen_appliances_android_java.activity;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.example.kitchen_appliances_android_java.R;
import com.example.kitchen_appliances_android_java.databinding.ActivityMainBinding;
import com.example.kitchen_appliances_android_java.fragment.AccountFragment;
import com.example.kitchen_appliances_android_java.fragment.CartFragment;
import com.example.kitchen_appliances_android_java.fragment.HistoryFragment;
import com.example.kitchen_appliances_android_java.fragment.HomeFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {
    private BottomNavigationView navView;
    ActivityMainBinding binding;
    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            item -> {
                int itemId = item.getItemId();
                Fragment selectedFragment = null;

                if (itemId == R.id.navigation_home) {
                    selectedFragment = new HomeFragment();
                } else if (itemId == R.id.navigation_cart) {
                    selectedFragment = new CartFragment();
                } else if (itemId == R.id.navigation_history) {
                    selectedFragment = new HistoryFragment();
                } else if (itemId == R.id.navigation_account) {
                    selectedFragment = new AccountFragment();
                }

                if (selectedFragment != null) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.appHostFragment, selectedFragment).commit();
                }


                return true;
            };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        BottomNavigationView bottomNav = findViewById(R.id.bottomMenu);
        bottomNav.setOnNavigationItemSelectedListener(navListener);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

    }
}