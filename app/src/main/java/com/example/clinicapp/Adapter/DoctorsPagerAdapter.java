package com.example.clinicapp.Adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.clinicapp.Fragment.DoctorsByCategoryFragment;

public class DoctorsPagerAdapter extends FragmentStateAdapter {

    private static final String[] CATEGORIES = {"General", "Dental", "Dermatology", "Pediatrics"};

    public DoctorsPagerAdapter(@NonNull Fragment host) {
        super(host);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return DoctorsByCategoryFragment.newInstance(CATEGORIES[position]);
    }

    @Override
    public int getItemCount() {
        return CATEGORIES.length;
    }

    // ⬇️ هذه الدالة هنا (وليس في DoctorAdapter)
    public static String tabTitle(int position) {
        switch (position) {
            case 0: return "عام";
            case 1: return "أسنان";
            case 2: return "جلدية";
            case 3: return "أطفال";
            default: return "";
        }
    }
}
