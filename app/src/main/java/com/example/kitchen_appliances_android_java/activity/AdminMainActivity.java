package com.example.kitchen_appliances_android_java.activity;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.kitchen_appliances_android_java.R;
import com.example.kitchen_appliances_android_java.databinding.ActivityMainBinding;
import com.example.kitchen_appliances_android_java.databinding.AdminActivityMainBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;


public class AdminMainActivity extends AppCompatActivity {
    private AdminActivityMainBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = AdminActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        BottomNavigationView navView = findViewById(R.id.bottomMenu);
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.admin_product, R.id.admin_order, R.id.admin_account)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.appHostFragment);
//        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);
    }

}
