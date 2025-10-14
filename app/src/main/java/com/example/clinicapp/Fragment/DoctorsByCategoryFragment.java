package com.example.clinicapp.Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.clinicapp.Adapter.DoctorAdapter;
import com.example.clinicapp.Database.MyDataBase;
import com.example.clinicapp.R;
import com.example.clinicapp.databinding.FragmentDoctorsByCategoryBinding;

public class DoctorsByCategoryFragment extends Fragment {

    private static final String ARG_CATEGORY = "arg_category";

    private FragmentDoctorsByCategoryBinding binding;
    private DoctorAdapter adapter;

    public static DoctorsByCategoryFragment newInstance(String category) {
        DoctorsByCategoryFragment f = new DoctorsByCategoryFragment();
        Bundle b = new Bundle();
        b.putString(ARG_CATEGORY, category);
        f.setArguments(b);
        return f;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentDoctorsByCategoryBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        // خذ الفئة من الـ arguments (افتراضي: General)
        final String category;
        if (getArguments() != null && getArguments().getString(ARG_CATEGORY) != null) {
            category = getArguments().getString(ARG_CATEGORY);
        } else {
            category = "General";
        }


        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerView.addItemDecoration(
                new DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL)
        );

        adapter = new DoctorAdapter(doctor -> {
            requireActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .setReorderingAllowed(true)
                    .replace(R.id.fragmentContainer, DoctorDetailsFragment.newInstance(doctor.getId()))
                    .addToBackStack("DoctorDetails")
                    .commit();
        });
        binding.recyclerView.setAdapter(adapter);

        // 📌 هنا كان الخطأ: لا تضع "Dental" ثابتة، استخدم المتغيّر category
        MyDataBase.getDatabase(requireContext())
                .doctorDao()
                .getByCategory(category)
                .observe(getViewLifecycleOwner(), list -> {
                    android.util.Log.d("Doctors", "category=" + category + ", count=" + (list == null ? 0 : list.size()));
                    adapter.setDoctors(list);
                });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
