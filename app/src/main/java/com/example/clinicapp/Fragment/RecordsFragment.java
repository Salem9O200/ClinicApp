package com.example.clinicapp.Fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.clinicapp.Adapter.RecordsAdapter;
import com.example.clinicapp.Database.MyDataBase;
import com.example.clinicapp.Model.Doctor;
import com.example.clinicapp.Model.MedicalRecord;
import com.example.clinicapp.R;
import com.example.clinicapp.databinding.FragmentRecordsBinding;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

public class RecordsFragment extends Fragment {

    private FragmentRecordsBinding binding;
    private RecordsAdapter adapter;
    private int userId = -1;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentRecordsBinding.inflate(inflater, container, false);

        adapter = new RecordsAdapter(new ArrayList<>());
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.recyclerView.setAdapter(adapter);
        binding.recyclerView.setItemAnimator(new DefaultItemAnimator());
        binding.recyclerView.addItemDecoration(new Spaces(12));

        if (binding.fabAdd != null) binding.fabAdd.setOnClickListener(v -> showAddDialog());

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        SharedPreferences prefs = requireActivity().getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
        userId = prefs.getInt("userId", -1);

        if (userId == -1) {
            showEmpty(true);
            Toast.makeText(requireContext(), "يرجى تسجيل الدخول أولاً", Toast.LENGTH_SHORT).show();
            return;
        }

        observeRecords();
    }

    private void observeRecords() {
        MyDataBase db = MyDataBase.getDatabase(requireContext());
        db.medicalRecordDao().getByUserLive(userId)
                .observe(getViewLifecycleOwner(), list -> {
                    if (list == null || list.isEmpty()) {
                        adapter.setItems(new ArrayList<>());
                        showEmpty(true);
                    } else {
                        Executors.newSingleThreadExecutor().execute(() -> {
                            List<RecordsAdapter.RecordItem> items = new ArrayList<>();
                            for (MedicalRecord r : list) {
                                Doctor d = db.doctorDao().getById(r.getDoctorId());
                                items.add(new RecordsAdapter.RecordItem(r, d));
                            }
                            if (!isAdded()) return;
                            requireActivity().runOnUiThread(() -> {
                                adapter.setItems(items);
                                showEmpty(false);
                            });
                        });
                    }
                });
    }

    private void showAddDialog() {
        if (userId == -1) {
            Toast.makeText(requireContext(), "سجّل الدخول أولاً", Toast.LENGTH_SHORT).show();
            return;
        }

        View v = LayoutInflater.from(requireContext())
                .inflate(R.layout.dialog_add_record, null, false);

        Spinner spDoctor = v.findViewById(R.id.spDoctor);
        TextInputEditText etTitle = v.findViewById(R.id.etTitle);
        TextInputEditText etNotes = v.findViewById(R.id.etNotes);

        AlertDialog dialog = new MaterialAlertDialogBuilder(requireContext())
                .setTitle("إضافة سجل طبي")
                .setView(v)
                .setNegativeButton("إلغاء", (d, w) -> d.dismiss())
                .setPositiveButton("حفظ", null)
                .create();

        Executors.newSingleThreadExecutor().execute(() -> {
            MyDataBase db = MyDataBase.getDatabase(requireContext());
            List<Doctor> doctors = db.doctorDao().getAllSync();

            if (doctors == null || doctors.isEmpty()) {
                requireActivity().runOnUiThread(() ->
                        Toast.makeText(requireContext(), "لا يوجد أطباء مضافين بعد", Toast.LENGTH_SHORT).show()
                );
                return;
            }

            List<String> names = new ArrayList<>();
            for (Doctor d : doctors) names.add(d.getName());

            requireActivity().runOnUiThread(() -> {
                spDoctor.setAdapter(new ArrayAdapter<>(
                        requireContext(), android.R.layout.simple_spinner_dropdown_item, names
                ));
                dialog.show();

                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(b -> {
                    int pos = spDoctor.getSelectedItemPosition();
                    String title = etTitle.getText() == null ? "" : etTitle.getText().toString().trim();
                    String notes = etNotes.getText() == null ? "" : etNotes.getText().toString().trim();

                    if (pos < 0 || pos >= doctors.size()) {
                        Toast.makeText(requireContext(), "اختر طبيبًا", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (title.isEmpty()) {
                        etTitle.setError("العنوان مطلوب");
                        return;
                    }

                    Doctor sel = doctors.get(pos);

                    Executors.newSingleThreadExecutor().execute(() -> {
                        // (اختياري) تأكيد صلاحية المستخدم
                        if (db.userDao().getById(userId) == null) {
                            requireActivity().runOnUiThread(() ->
                                    Toast.makeText(requireContext(), "الرجاء تسجيل الدخول مجددًا", Toast.LENGTH_LONG).show()
                            );
                            return;
                        }

                        MedicalRecord rec = new MedicalRecord();
                        rec.setUserId(userId);
                        rec.setDoctorId(sel.getId());
                        rec.setTitle(title);
                        rec.setNotes(notes);
                        db.medicalRecordDao().insert(rec);

                        requireActivity().runOnUiThread(dialog::dismiss);
                    });
                });
            });
        });
    }

    private void showEmpty(boolean empty) {
        if (binding == null) return;
        if (binding.emptyState != null) binding.emptyState.setVisibility(empty ? View.VISIBLE : View.GONE);
        binding.recyclerView.setVisibility(empty ? View.GONE : View.VISIBLE);
        if (binding.fabAdd != null) binding.fabAdd.setVisibility(View.VISIBLE);
    }

    static class Spaces extends RecyclerView.ItemDecoration {
        final int dp;
        Spaces(int dp) { this.dp = dp; }
        @Override public void getItemOffsets(@NonNull Rect out, @NonNull View v, @NonNull RecyclerView p, @NonNull RecyclerView.State s) {
            int sp = Math.round(dp * v.getResources().getDisplayMetrics().density);
            out.left = sp; out.right = sp; out.bottom = sp;
            if (p.getChildAdapterPosition(v) == 0) out.top = sp;
        }
    }
}
