package com.example.clinicapp.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.clinicapp.Model.Appointment;
import com.example.clinicapp.Model.Doctor;
import com.example.clinicapp.R;

import java.util.List;

public class AppointmentAdapter extends RecyclerView.Adapter<AppointmentAdapter.ViewHolder> {

    public static class AppointmentItem {
        public final Appointment appointment;
        public final Doctor doctor;
        public AppointmentItem(Appointment appointment, Doctor doctor) {
            this.appointment = appointment;
            this.doctor = doctor;
        }
    }

    private List<AppointmentItem> items;

    public AppointmentAdapter(List<AppointmentItem> items) {
        this.items = items;
    }

    public void setItems(List<AppointmentItem> newItems) {
        this.items = newItems;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public AppointmentAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_appointment, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull AppointmentAdapter.ViewHolder holder, int position) {
        AppointmentItem item = items.get(position);

        String doctorName = (item.doctor != null && item.doctor.getName() != null)
                ? item.doctor.getName() : "غير معروف";

        String specialty = (item.doctor != null && item.doctor.getCategory() != null)
                ? toArabic(item.doctor.getCategory()) : "—";

        holder.tvDoctorName.setText(doctorName);
        holder.tvSpecialty.setText(specialty);
        holder.tvDate.setText("الموعد: " + item.appointment.getSlot());
        holder.tvStatus.setText("الحالة: " + item.appointment.getStatus());

        // لو عندك ImageView في layout
        if (holder.ivDoctor != null) {
            holder.ivDoctor.setImageResource(R.drawable.ic_doctor_peds); // أيقونة افتراضية
        }
    }

    @Override
    public int getItemCount() {
        return (items != null) ? items.size() : 0;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivDoctor;
        TextView tvDoctorName, tvSpecialty, tvDate, tvStatus;

        ViewHolder(View itemView) {
            super(itemView);
            ivDoctor     = itemView.findViewById(R.id.ivDoctor);      // لو مش موجودة في XML عادي تبقى null
            tvDoctorName = itemView.findViewById(R.id.tvDoctorName);
            tvSpecialty  = itemView.findViewById(R.id.tvSpecialty);
            tvDate       = itemView.findViewById(R.id.tvDate);
            tvStatus     = itemView.findViewById(R.id.tvStatus);
        }
    }
    public AppointmentItem getItemAt(int position) {
        if (position < 0 || position >= items.size()) return null;
        return items.get(position);
    }

    private static String toArabic(String c) {
        if (c == null) return "";
        switch (c) {
            case "General":      return "طبيب عام";
            case "Dental":       return "طبيب أسنان";
            case "Dermatology":  return "أخصائي جلدية";
            case "Pediatrics":   return "طبيب أطفال";
            default:             return c;
        }
    }
}
