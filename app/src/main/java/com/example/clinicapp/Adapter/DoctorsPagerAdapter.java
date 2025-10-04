package com.example.clinicapp.Adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.clinicapp.Fragment.DentalDoctorsFragment;
import com.example.clinicapp.Fragment.DermatologyDoctorsFragment;
import com.example.clinicapp.Fragment.GeneralDoctorsFragment;
import com.example.clinicapp.Fragment.PediatricsDoctorsFragment;

public class DoctorsPagerAdapter extends FragmentStateAdapter {
    public DoctorsPagerAdapter(@NonNull Fragment host) { super(host); }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0: return new GeneralDoctorsFragment();
            case 1: return new DentalDoctorsFragment();
            case 2: return new DermatologyDoctorsFragment();
            default: return new PediatricsDoctorsFragment();
        }
    }

    @Override public int getItemCount() { return 4; }
}
