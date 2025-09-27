package com.example.clinicapp.Adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.clinicapp.Fragment.Onboarding1Fragment;
import com.example.clinicapp.Fragment.Onboarding2Fragment;
import com.example.clinicapp.Fragment.Onboarding3Fragment;

public class OnboardingAdapter extends FragmentStateAdapter {

    public OnboardingAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 1: return new Onboarding2Fragment();
            case 2: return new Onboarding3Fragment();
            default: return new Onboarding1Fragment();
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
