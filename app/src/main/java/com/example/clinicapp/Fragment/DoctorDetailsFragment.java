package com.example.clinicapp.Fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.clinicapp.Database.MyDataBase;
import com.example.clinicapp.Model.Doctor;
import com.example.clinicapp.R;
import com.example.clinicapp.databinding.FragmentDoctorDetailsBinding;

import java.util.concurrent.Executors;

public class DoctorDetailsFragment extends Fragment {

    private static final String ARG_DOCTOR_ID = "doctor_id";
    private FragmentDoctorDetailsBinding binding;
    private int doctorId;

    public static DoctorDetailsFragment newInstance(int doctorId) {
        DoctorDetailsFragment f = new DoctorDetailsFragment();
        Bundle b = new Bundle();
        b.putInt(ARG_DOCTOR_ID, doctorId);
        f.setArguments(b);
        return f;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentDoctorDetailsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        doctorId = getArguments() != null ? getArguments().getInt(ARG_DOCTOR_ID, -1) : -1;

        if (doctorId == -1) {
            requireActivity().onBackPressed();
            return;
        }

        Executors.newSingleThreadExecutor().execute(() -> {
            Doctor d = MyDataBase.getDatabase(requireContext()).doctorDao().getById(doctorId);
            if (!isAdded()) return;
            requireActivity().runOnUiThread(() -> bindDoctor(d));
        });
    }

    private void bindDoctor(Doctor d) {
        if (d == null) {
            requireActivity().onBackPressed();
            return;
        }
        binding.tvName.setText(d.getName());
        binding.tvCategory.setText(categoryArabic(d.getCategory()));
        binding.tvSlots.setText(d.getSlotsCsv() == null ? "-" : d.getSlotsCsv());
        binding.tvPhone.setText(d.getPhone() == null ? "-" : d.getPhone());

        int resId = 0;
        if (d.getImageUrl() != null && !d.getImageUrl().trim().isEmpty()) {
            resId = getResources().getIdentifier(d.getImageUrl(), "drawable", requireContext().getPackageName());
        }
        if (resId == 0) resId = R.drawable.doctor;

        Glide.with(this).load(resId).placeholder(R.drawable.doctor).into(binding.ivDoctor);

        // الاتصال
        binding.btnCall.setOnClickListener(v -> {
            String phone = d.getPhone();
            if (phone != null && !phone.trim().isEmpty()) {
                Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phone));
                startActivity(intent);
            }
        });

        // الموقع (ابحث بالاسم على الخرائط – عدّلها لإحداثيات لو بدك)
        binding.btnMap.setOnClickListener(v -> {
            String query = Uri.encode(d.getName());
            Uri gmmIntentUri = Uri.parse("geo:0,0?q=" + query);
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            mapIntent.setPackage("com.google.android.apps.maps"); // اختياري
            startActivity(mapIntent);
        });
    }

    private String categoryArabic(String c) {
        if (c == null) return "";
        switch (c) {
            case "General": return "طب عام";
            case "Dental": return "أسنان";
            case "Dermatology": return "جلدية";
            case "Pediatrics": return "أطفال";
            default: return c;
        }
    }
}
