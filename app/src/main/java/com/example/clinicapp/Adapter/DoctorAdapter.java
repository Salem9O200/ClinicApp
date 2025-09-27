package com.example.clinicapp.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.clinicapp.Model.Doctor;
import com.example.clinicapp.R;

import java.util.List;

public class DoctorAdapter extends RecyclerView.Adapter<DoctorAdapter.ViewHolder> {
    private List<Doctor> doctors; // ✅
    private OnDoctorClickListener listener;

    public interface OnDoctorClickListener {
        void onDoctorClick(Doctor doctor); // ✅
    }

    public DoctorAdapter(List<Doctor> doctors, OnDoctorClickListener listener) { // ✅
        this.doctors = doctors;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_doctor, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Doctor doctor = doctors.get(position); // ✅
        holder.tvName.setText(doctor.getName());
        holder.tvSpecialty.setText(getSpecialtyArabic(doctor.getSpecialty()));
        holder.tvSlots.setText("المواعيد: " + doctor.getAvailableSlots());

        Glide.with(holder.ivDoctor.getContext())
                .load(R.drawable.ic_launcher_background)
                .placeholder(R.drawable.ic_launcher_background)
                .into(holder.ivDoctor);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDoctorClick(doctor);
            }
        });
    }

    private String getSpecialtyArabic(String specialty) {
        switch (specialty) {
            case "General": return "طبيب عام";
            case "Dental": return "طبيب أسنان";
            case "Dermatology": return "أخصائي جلدية";
            case "Pediatrics": return "طبيب أطفال";
            default: return specialty;
        }
    }

    @Override
    public int getItemCount() {
        return doctors.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivDoctor;
        TextView tvName, tvSpecialty, tvSlots;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivDoctor = itemView.findViewById(R.id.ivDoctor);
            tvName = itemView.findViewById(R.id.tvName);
            tvSpecialty = itemView.findViewById(R.id.tvSpecialty);
            tvSlots = itemView.findViewById(R.id.tvSlots);
        }
    }
}