package com.example.clinicapp.Fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.ImageView;

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
        Bundle args = new Bundle();
        args.putInt(ARG_DOCTOR_ID, doctorId);
        f.setArguments(args);
        return f;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentDoctorDetailsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        doctorId = getArguments() != null ? getArguments().getInt(ARG_DOCTOR_ID, -1) : -1;

        if (doctorId == -1) {
            binding.tvName.setText("لم يتم العثور على الطبيب");
            return;
        }

        // تحميل بيانات الطبيب من قاعدة البيانات بالخلفية
        Executors.newSingleThreadExecutor().execute(() -> {
            Doctor doctor = MyDataBase.getDatabase(requireContext()).doctorDao().getById(doctorId);

            if (doctor == null || !isAdded()) return;

            requireActivity().runOnUiThread(() -> showDoctorDetails(doctor));
        });
    }

    private void showDoctorDetails(Doctor doctor) {
        binding.tvName.setText(doctor.getName());
        binding.chipCategory.setText(getCategoryArabic(doctor.getCategory()));
        binding.tvPhone.setText("📞 " + (doctor.getPhone() == null ? "-" : doctor.getPhone()));
        binding.tvSlots.setText("🕐 المواعيد: " + (doctor.getSlotsCsv() == null ? "-" : doctor.getSlotsCsv()));

        int resId = 0;

// لو Doctor عنده resource id مباشر (int)
        int imgRes = doctor.getImageRes(); // لو يرجع 0 يعني مفيش صورة
        if (imgRes != 0) {
            resId = imgRes;
        } else {
            // (اختياري) لو عندك أيضاً دعم اسم صورة كنص
            String url = String.valueOf(doctor.getImageRes()); // فقط إذا كان موجود في الكلاس
            if (url != null && !url.trim().isEmpty()) {
                if (url.startsWith("http")) {
                    Glide.with(requireContext())
                            .load(url)
                            .placeholder(R.drawable.ic_launcher_background)
                            .error(R.drawable.ic_launcher_background)
                            .into(binding.ivDoctor);
                    return;
                } else {
                    resId = getResources().getIdentifier(url, "drawable", requireContext().getPackageName());
                }
            }
        }

        if (resId == 0) resId = R.drawable.ic_launcher_background;

        Glide.with(requireContext())
                .load(resId)
                .placeholder(R.drawable.ic_launcher_background)
                .error(R.drawable.ic_launcher_background)
                .into(binding.ivDoctor);


        binding.btnCall.setOnClickListener(v -> {
            if (doctor.getPhone() != null && !doctor.getPhone().trim().isEmpty()) {
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + doctor.getPhone()));
                startActivity(intent);
            }
        });

        binding.btnMap.setOnClickListener(v -> {
            String query = Uri.encode(doctor.getName() + " عيادة");
            Uri mapUri = Uri.parse("geo:0,0?q=" + query);
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, mapUri);
            // جرّب Google Maps أولاً، ولو مش موجود افتح أي تطبيق مناسب
            try {
                mapIntent.setPackage("com.google.android.apps.maps");
                startActivity(mapIntent);
            } catch (Exception e) {
                mapIntent.setPackage(null);
                startActivity(mapIntent);
            }
        });

    }

    private String getCategoryArabic(String category) {
        if (category == null) return "";
        switch (category) {
            case "General": return "طب عام";
            case "Dental": return "أسنان";
            case "Dermatology": return "جلدية";
            case "Pediatrics": return "أطفال";
            default: return category;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
