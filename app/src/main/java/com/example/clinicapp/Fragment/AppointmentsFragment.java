package com.example.clinicapp.Fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.clinicapp.Adapter.AppointmentAdapter;
import com.example.clinicapp.Database.MyDataBase;
import com.example.clinicapp.Model.Appointment;
import com.example.clinicapp.Model.Doctor;
import com.example.clinicapp.Model.User;
import com.example.clinicapp.R;
import com.example.clinicapp.databinding.FragmentAppointmentsBinding;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;

public class AppointmentsFragment extends Fragment {

    private FragmentAppointmentsBinding binding;
    private AppointmentAdapter adapter;
    private int userId = -1;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        binding = FragmentAppointmentsBinding.inflate(inflater, container, false);

        // RecyclerView
        adapter = new AppointmentAdapter(new ArrayList<>());
        binding.rvAppointments.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvAppointments.setAdapter(adapter);
        binding.rvAppointments.setItemAnimator(new DefaultItemAnimator());
        binding.rvAppointments.addItemDecoration(new SpacesItemDecoration(12));
        binding.rvAppointments.addItemDecoration(
                new DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL)
        );

        // أزرار الإضافة
        if (binding.fabAdd != null) binding.fabAdd.setOnClickListener(v -> showAddDialog());

        // Swipe to refresh (لو موجود في الـ XML)
        if (binding.swipe != null) {
            binding.swipe.setOnRefreshListener(() -> binding.swipe.setRefreshing(false));
        }

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

        observeAppointments();
    }

    private void observeAppointments() {
        MyDataBase db = MyDataBase.getDatabase(requireContext());
        db.appointmentDao().getAppointmentsByUserLive(userId)
                .observe(getViewLifecycleOwner(), appointments -> {
                    if (appointments == null || appointments.isEmpty()) {
                        adapter.setItems(new ArrayList<>());
                        showEmpty(true);
                    } else {
                        // اجلب بيانات الطبيب لكل موعد بالخلفية
                        Executors.newSingleThreadExecutor().execute(() -> {
                            List<AppointmentAdapter.AppointmentItem> items = new ArrayList<>();
                            for (Appointment app : appointments) {
                                Doctor doctor = db.doctorDao().getById(app.getDoctorId());
                                items.add(new AppointmentAdapter.AppointmentItem(app, doctor));
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

        View dialogView = LayoutInflater.from(requireContext())
                .inflate(R.layout.dialog_add_appointment, null, false);
        Spinner spDoctor = dialogView.findViewById(R.id.spDoctor);
        Spinner spSlot   = dialogView.findViewById(R.id.spSlot);

        AlertDialog dialog = new MaterialAlertDialogBuilder(requireContext())
                .setTitle("إضافة موعد")
                .setView(dialogView)
                .setNegativeButton("إلغاء", (d, w) -> d.dismiss())
                .setPositiveButton("حفظ", null) // سنستبدله بعد show() لمنع الإغلاق المبكر
                .create();

        // حمّل الأطباء بالخلفية
        Executors.newSingleThreadExecutor().execute(() -> {
            MyDataBase db = MyDataBase.getDatabase(requireContext());
            List<Doctor> doctors = db.doctorDao().getAllSync();

            // لا يوجد أطباء
            if (doctors == null || doctors.isEmpty()) {
                requireActivity().runOnUiThread(() ->
                        Toast.makeText(requireContext(), "لا يوجد أطباء مضافين بعد", Toast.LENGTH_SHORT).show()
                );
                return;
            }

            List<String> names = new ArrayList<>();
            for (Doctor d : doctors) names.add(d.getName());

            requireActivity().runOnUiThread(() -> {
                ArrayAdapter<String> doctorAdapter = new ArrayAdapter<>(
                        requireContext(), android.R.layout.simple_spinner_dropdown_item, names);
                spDoctor.setAdapter(doctorAdapter);

                // Preselect أول طبيب وتعبئة الـ slots مباشرة
                if (!doctors.isEmpty()) {
                    Doctor first = doctors.get(0);
                    List<String> slots0 = parseSlots(first.getSlotsCsv());
                    ArrayAdapter<String> slotAdapter0 = new ArrayAdapter<>(
                            requireContext(), android.R.layout.simple_spinner_dropdown_item, slots0);
                    spSlot.setAdapter(slotAdapter0);
                }

                spDoctor.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        if (position < 0 || position >= doctors.size()) return;
                        Doctor sel = doctors.get(position);
                        List<String> slots = parseSlots(sel.getSlotsCsv());
                        ArrayAdapter<String> slotAdapter = new ArrayAdapter<>(
                                requireContext(), android.R.layout.simple_spinner_dropdown_item, slots);
                        spSlot.setAdapter(slotAdapter);
                    }
                    @Override public void onNothingSelected(AdapterView<?> parent) {}
                });

                dialog.show();

                // منع إغلاق الدايالوج قبل التحقق
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
                    int pos = spDoctor.getSelectedItemPosition();
                    if (pos < 0 || pos >= doctors.size()) {
                        Toast.makeText(requireContext(), "اختر طبيبًا", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    String slot = (String) spSlot.getSelectedItem();
                    if (slot == null || slot.trim().isEmpty()) {
                        Toast.makeText(requireContext(), "اختر وقت الموعد", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    Doctor selected = doctors.get(pos);

                    // تحقق من التعارض + إدراج في الخلفية
                    Executors.newSingleThreadExecutor().execute(() -> {
                        // تأكد المستخدم موجود
                        User u = db.userDao().getById(userId);
                        if (u == null) {
                            requireActivity().runOnUiThread(() -> {
                                Toast.makeText(requireContext(), "جلستك غير صالحة، سجّل الدخول مجددًا", Toast.LENGTH_LONG).show();
                                // ممكن تمسح الجلسة وتخرج لواجهة الدخول
                                requireActivity().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
                                        .edit().clear().apply();

                            });
                            return;
                        }

                        // تأكد الدكتور موجود (تحسّبًا)
                        Doctor real = db.doctorDao().getById(selected.getId());
                        if (real == null) {
                            requireActivity().runOnUiThread(() ->
                                    Toast.makeText(requireContext(), "الطبيب المحدد غير موجود", Toast.LENGTH_SHORT).show()
                            );
                            return;
                        }

                        // تحقق التعارض
                        Appointment conflict = db.appointmentDao()
                                .getAppointmentByDoctorAndTime(selected.getId(), slot);
                        if (conflict != null) {
                            requireActivity().runOnUiThread(() ->
                                    Toast.makeText(requireContext(), "هذا الوقت محجوز لهذا الطبيب", Toast.LENGTH_SHORT).show()
                            );
                            return;
                        }

                        // الإدراج الآمن
                        Appointment a = new Appointment();
                        a.setUserId(userId);
                        a.setDoctorId(selected.getId());
                        a.setSlot(slot);
                        a.setStatus("Pending");
                        db.appointmentDao().insert(a);

                        requireActivity().runOnUiThread(dialog::dismiss);
                    });

                });
            });
        });
    }

    private List<String> parseSlots(String csv) {
        List<String> out = new ArrayList<>();
        if (csv == null || csv.trim().isEmpty()) return out;
        for (String s : csv.split(",")) {
            String t = s.trim();
            if (!t.isEmpty()) out.add(t);
        }
        return out;
    }

    private void showEmpty(boolean empty) {
        if (binding == null) return;
        // إن وُجدت حاوية كاملة للحالة الفارغة
        if (binding.emptyState != null) {
            binding.emptyState.setVisibility(empty ? View.VISIBLE : View.GONE);
        } else if (binding.ivEmpty != null) {
            // بديل: صورة فقط (حل مؤقت)
            binding.ivEmpty.setVisibility(empty ? View.VISIBLE : View.GONE);
        }
        binding.rvAppointments.setVisibility(empty ? View.GONE : View.VISIBLE);
        // (اختياري) دائماً أظهر الـ FAB
        if (binding.fabAdd != null) {
            binding.fabAdd.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    /** مسافة لطيفة بين العناصر */
    static class SpacesItemDecoration extends RecyclerView.ItemDecoration {
        private final int spaceDp;
        SpacesItemDecoration(int spaceDp) { this.spaceDp = spaceDp; }
        @Override
        public void getItemOffsets(@NonNull Rect outRect, @NonNull View view,
                                   @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
            int s = dp(view, spaceDp);
            outRect.left = s;
            outRect.right = s;
            outRect.bottom = s;
            if (parent.getChildAdapterPosition(view) == 0) outRect.top = s;
        }
        private int dp(View v, int dp) {
            float d = v.getResources().getDisplayMetrics().density;
            return Math.round(dp * d);
        }
    }
}
