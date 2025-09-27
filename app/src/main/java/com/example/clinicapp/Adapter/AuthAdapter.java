package com.example.clinicapp.Adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.clinicapp.Fragment.LoginFragment;
import com.example.clinicapp.Fragment.RegisterFragment;

public class AuthAdapter extends FragmentStateAdapter {
    public AuthAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (position == 0) {
            return new LoginFragment();
        } else {
            return new RegisterFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
