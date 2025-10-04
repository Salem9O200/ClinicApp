package com.example.clinicapp.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.clinicapp.Adapter.DoctorAdapter;
import com.example.clinicapp.Database.MyDataBase;
import com.example.clinicapp.Model.Doctor;
import com.example.clinicapp.R;
import com.example.clinicapp.databinding.FragmentPediatricsDoctorsBinding;

public class PediatricsDoctorsFragment extends Fragment {
    private FragmentPediatricsDoctorsBinding binding;
    private DoctorAdapter adapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentPediatricsDoctorsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new DoctorAdapter(doctor -> {
            // افتح شاشة التفاصيل
            getParentFragmentManager().beginTransaction()
                    .replace(R.id.fragmentContainer, DoctorDetailsFragment.newInstance(doctor.getId()))
                    .addToBackStack(null)
                    .commit();
        });
        binding.recyclerView.setAdapter(adapter);

// راقب البيانات من Room (LiveData)
        MyDataBase.getDatabase(requireContext())
                .doctorDao().getByCategory("Dental")
                .observe(getViewLifecycleOwner(), adapter::setDoctors);

    }
}
