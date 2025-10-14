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

import java.util.ArrayList;
import java.util.List;

public class DoctorAdapter extends RecyclerView.Adapter<DoctorAdapter.ViewHolder> {

    private final List<Doctor> doctors = new ArrayList<>();
    private final OnDoctorClickListener listener;

    // Interface لحدث الضغط
    public interface OnDoctorClickListener {
        void onDoctorClick(Doctor doctor);
    }

    public DoctorAdapter(OnDoctorClickListener listener) {
        this.listener = listener;
    }

    public void setDoctors(List<Doctor> list) {
        doctors.clear();
        if (list != null) doctors.addAll(list);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_doctor, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Doctor d = doctors.get(position);

        holder.tvName.setText(d.getName());
        holder.tvCategory.setText(getCategoryArabic(d.getCategory()));
        holder.tvSlots.setText("المواعيد: " + (d.getSlotsCsv() == null ? "-" : d.getSlotsCsv()));
        holder.tvPhone.setText("📞 " + (d.getPhone() == null ? "-" : d.getPhone()));

        // تحميل صورة من drawable بالاسم (بدون الامتداد)
        String drawableName = String.valueOf(d.getImageRes());
        int resId = 0;
        if (drawableName != null && !drawableName.trim().isEmpty()) {
            resId = holder.itemView.getResources().getIdentifier(
                    drawableName, "drawable", holder.itemView.getContext().getPackageName()
            );
        }
        if (resId == 0) resId = R.drawable.ic_launcher_background;

        Glide.with(holder.ivDoctor.getContext())
                .load(resId)
                .placeholder(R.drawable.ic_launcher_background)
                .into(holder.ivDoctor);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onDoctorClick(d);
        });
    }


    @Override
    public int getItemCount() {
        return doctors.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView ivDoctor;
        TextView tvName, tvCategory, tvSlots, tvPhone;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            ivDoctor   = itemView.findViewById(R.id.ivDoctor);
            tvName     = itemView.findViewById(R.id.tvName);
            tvCategory = itemView.findViewById(R.id.tvCategory);
            tvSlots    = itemView.findViewById(R.id.tvSlots);
            tvPhone    = itemView.findViewById(R.id.tvPhone);
        }
    }

    // ترجمة التصنيف للعربية
    private String getCategoryArabic(String category) {
        if (category == null) return "";
        switch (category) {
            case "General":      return "طب عام";
            case "Dental":       return "أسنان";
            case "Dermatology":  return "جلدية";
            case "Pediatrics":   return "أطفال";
            default:             return category;
        }
    }
}
