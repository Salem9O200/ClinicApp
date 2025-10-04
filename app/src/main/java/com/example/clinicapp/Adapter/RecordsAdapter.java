package com.example.clinicapp.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.text.format.DateFormat;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.clinicapp.Model.Doctor;
import com.example.clinicapp.Model.MedicalRecord;
import com.example.clinicapp.R;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class RecordsAdapter extends RecyclerView.Adapter<RecordsAdapter.VH> {

    public static class RecordItem {
        public final MedicalRecord record;
        public final Doctor doctor;
        public RecordItem(MedicalRecord record, Doctor doctor) {
            this.record = record;
            this.doctor = doctor;
        }
    }

    private List<RecordItem> items = new ArrayList<>();

    public RecordsAdapter(List<RecordItem> items) { setItems(items); }

    public void setItems(List<RecordItem> data) {
        this.items = data != null ? data : new ArrayList<>();
        notifyDataSetChanged();
    }

    @NonNull @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_record, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int pos) {
        RecordItem it = items.get(pos);
        h.tvTitle.setText(it.record.getTitle());
        h.tvDoctor.setText(it.doctor != null ? it.doctor.getName() : "—");
        h.tvNotes.setText(it.record.getNotes() == null ? "" : it.record.getNotes());

        String date = DateFormat.format("yyyy-MM-dd  HH:mm", new Date(it.record.getCreatedAt())).toString();
        h.tvDate.setText(date);
    }

    @Override
    public int getItemCount() { return items.size(); }

    static class VH extends RecyclerView.ViewHolder {
        TextView tvTitle, tvDoctor, tvDate, tvNotes;
        VH(@NonNull View v) {
            super(v);
            tvTitle  = v.findViewById(R.id.tvTitle);
            tvDoctor = v.findViewById(R.id.tvDoctor);
            tvDate   = v.findViewById(R.id.tvDate);
            tvNotes  = v.findViewById(R.id.tvNotes);
        }
    }
}
