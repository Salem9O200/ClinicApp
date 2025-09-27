package com.example.clinicapp;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.clinicapp.Adapter.AuthAdapter;
import com.example.clinicapp.databinding.ActivityAuthBinding;
import com.google.android.material.tabs.TabLayoutMediator;

public class AuthActivity extends AppCompatActivity {

    ActivityAuthBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAuthBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

            AuthAdapter adapter = new AuthAdapter(this);
            binding.viewPager.setAdapter(adapter);

            new TabLayoutMediator(binding.tabLayout, binding.viewPager, (tab, position) -> {
                if (position == 0) {
                    tab.setText("تسجيل الدخول");
                } else {
                    tab.setText("إنشاء حساب");
                }
            }).attach();
        }
    }
