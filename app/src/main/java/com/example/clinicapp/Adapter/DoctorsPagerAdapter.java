package com.example.clinicapp.Adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.clinicapp.Fragment.DoctorsByCategoryFragment;

public class DoctorsPagerAdapter extends FragmentStateAdapter {

    // القيم يجب أن تطابق تماما ما في قاعدة البيانات (Doctor.category)
    private static final String[] CATEGORIES = new String[] {
            "General", "Dental", "Dermatology", "Pediatrics"
    };

    public DoctorsPagerAdapter(@NonNull Fragment host) {
        super(host);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        String category = CATEGORIES[position];
        return DoctorsByCategoryFragment.newInstance(category);
    }

    @Override
    public int getItemCount() {
        return CATEGORIES.length;
    }

    public static String getTabTitle(int position) {
        switch (position) {
            case 0: return "عام";
            case 1: return "أسنان";
            case 2: return "جلدية";
            case 3: return "أطفال";
            default: return "أخرى";
        }
    }
}
