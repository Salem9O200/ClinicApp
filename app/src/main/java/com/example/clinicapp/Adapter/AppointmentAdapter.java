package com.example.clinicapp.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.clinicapp.Model.Appointment;
import com.example.clinicapp.Model.Doctor;
import com.example.clinicapp.R;
import java.util.List;

public class AppointmentAdapter extends RecyclerView.Adapter<AppointmentAdapter.ViewHolder> {
    private List<AppointmentItem> appointments;

    // كلاس داخلي لتمثيل العنصر
    public static class AppointmentItem {
        public Appointment appointment;
        public Doctor doctor;

        public AppointmentItem(Appointment appointment, Doctor doctor) {
            this.appointment = appointment;
            this.doctor = doctor;
        }
    }

    public AppointmentAdapter(List<AppointmentItem> appointments) {
        this.appointments = appointments;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_appointment, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AppointmentItem item = appointments.get(position);
        holder.tvDoctorName.setText(item.doctor.getName());
        holder.tvDateTime.setText("الموعد: " + item.appointment.getDateTime());

        // تعيين لون ونص الحالة
        String status = item.appointment.getStatus();
        holder.tvStatus.setText("حالة الموعد: " + getStatusArabic(status));
        if ("Completed".equals(status)) {
            holder.tvStatus.setTextColor(holder.itemView.getContext().getResources().getColor(android.R.color.holo_green_dark));
        } else if ("Upcoming".equals(status)) {
            holder.tvStatus.setTextColor(holder.itemView.getContext().getResources().getColor(android.R.color.holo_blue_dark));
        } else {
            holder.tvStatus.setTextColor(holder.itemView.getContext().getResources().getColor(android.R.color.darker_gray));
        }
    }

    private String getStatusArabic(String status) {
        switch (status) {
            case "Upcoming": return "معلق";
            case "Completed": return "مكتمل";
            case "Cancelled": return "ملغى";
            default: return status;
        }
    }

    @Override
    public int getItemCount() {
        return appointments.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvDoctorName, tvDateTime, tvStatus;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDoctorName = itemView.findViewById(R.id.tvDoctorName);
            tvDateTime = itemView.findViewById(R.id.tvDateTime);
            tvStatus = itemView.findViewById(R.id.tvStatus);
        }
    }
}