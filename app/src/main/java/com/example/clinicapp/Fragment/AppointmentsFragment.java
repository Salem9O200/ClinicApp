package com.example.clinicapp.Fragment;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.clinicapp.Adapter.AppointmentAdapter;
import com.example.clinicapp.Database.MyDataBase;
import com.example.clinicapp.Model.Appointment;
import com.example.clinicapp.Model.Doctor;
import com.example.clinicapp.databinding.FragmentAppointmentsBinding;

import java.util.ArrayList;
import java.util.List;

public class AppointmentsFragment extends Fragment {

    private FragmentAppointmentsBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentAppointmentsBinding.inflate(inflater, container, false);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        SharedPreferences prefs = requireActivity().getSharedPreferences("app_prefs", getContext().MODE_PRIVATE);
        int userId = prefs.getInt("userId", -1);

        if (userId == -1) {
            return binding.getRoot();
        }

        new Thread(() -> {
            MyDataBase db = MyDataBase.getDatabase(requireContext());
            List<Appointment> appointments = db.appointmentDao().getAppointmentsByUser(userId);
            List<AppointmentAdapter.AppointmentItem> items = new ArrayList<>();

            for (Appointment app : appointments) {
                Doctor doctor = db.doctorDao().getDoctorById(app.getDoctorId());
                items.add(new AppointmentAdapter.AppointmentItem(app, doctor));
            }

            requireActivity().runOnUiThread(() -> {
                AppointmentAdapter adapter = new AppointmentAdapter(items);
                binding.recyclerView.setAdapter(adapter);
            });
        }).start();

        return binding.getRoot();
    }
}