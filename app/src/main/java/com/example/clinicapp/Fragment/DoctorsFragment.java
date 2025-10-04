package com.example.clinicapp.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.clinicapp.Adapter.DoctorsPagerAdapter;
import com.example.clinicapp.databinding.FragmentDoctorsBinding;
import com.google.android.material.tabs.TabLayoutMediator;

public class DoctorsFragment extends Fragment {

    private FragmentDoctorsBinding binding;

    public DoctorsFragment() {}

    public static DoctorsFragment newInstance(String p1, String p2) {
        DoctorsFragment fragment = new DoctorsFragment();
        Bundle args = new Bundle();
        args.putString("param1", p1);
        args.putString("param2", p2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentDoctorsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        DoctorsPagerAdapter adapter = new DoctorsPagerAdapter(this);
        binding.viewPager.setAdapter(adapter);

        new TabLayoutMediator(binding.tabLayout, binding.viewPager,
                (tab, position) -> {
                    switch (position) {
                        case 0: tab.setText("عام"); break;
                        case 1: tab.setText("أسنان"); break;
                        case 2: tab.setText("جلدية"); break;
                        case 3: tab.setText("أطفال"); break;
                    }
                }).attach();

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
