package com.example.clinicapp.Fragment;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.os.Build;
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
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.clinicapp.Adapter.AppointmentAdapter;
import com.example.clinicapp.Database.MyDataBase;
import com.example.clinicapp.MainActivity; // غيّرها إن كانت شاشة أخرى هي الوجهة
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

    private static final String CHANNEL_ID = "reminder_channel";
    private static final int REQ_POST_NOTI = 2001;

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

        // Swipe-to-delete مع تأكيد + إشعار بعد الحذف
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0,
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            @Override public boolean onMove(@NonNull RecyclerView rv, @NonNull RecyclerView.ViewHolder vh, @NonNull RecyclerView.ViewHolder tgt) {
                return false;
            }

            @Override public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int pos = viewHolder.getBindingAdapterPosition();
                AppointmentAdapter.AppointmentItem item = adapter.getItemAt(pos);
                if (item == null) { adapter.notifyItemChanged(pos); return; }

                new MaterialAlertDialogBuilder(requireContext())
                        .setTitle("تأكيد الحذف")
                        .setMessage("هل تريد حذف هذا الموعد؟")
                        .setNegativeButton("إلغاء", (d,w) -> {
                            d.dismiss();
                            adapter.notifyItemChanged(pos); // أعد العنصر مكانه
                        })
                        .setPositiveButton("حذف", (d,w) -> {
                            Executors.newSingleThreadExecutor().execute(() -> {
                                int rows = MyDataBase.getDatabase(requireContext())
                                        .appointmentDao()
                                        .deleteById(item.appointment.getId());

                                requireActivity().runOnUiThread(() -> {
                                    if (rows > 0) {
                                        Toast.makeText(requireContext(), "تم حذف الموعد", Toast.LENGTH_SHORT).show();
                                        // إشعار بعد الحذف
                                        showNotification("تم الحذف", "تم حذف الموعد بنجاح ❌");
                                    } else {
                                        Toast.makeText(requireContext(), "فشل الحذف", Toast.LENGTH_SHORT).show();
                                        adapter.notifyItemChanged(pos);
                                    }
                                });
                            });
                        })
                        .show();
            }
        }).attachToRecyclerView(binding.rvAppointments);

        // زر الإضافة
        if (binding.fabAdd != null) {
            binding.fabAdd.setOnClickListener(v -> showAddDialog());
        }

        // Swipe refresh (إن وجد)
        if (binding.swipe != null) {
            binding.swipe.setOnRefreshListener(() -> binding.swipe.setRefreshing(false));
        }

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        ensureNotificationChannel();
        ensurePostNotificationsPermission();

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
                .setPositiveButton("حفظ", null)
                .create();

        Executors.newSingleThreadExecutor().execute(() -> {
            MyDataBase db = MyDataBase.getDatabase(requireContext());
            List<Doctor> doctors = db.doctorDao().getAllSync();

            if (doctors == null || doctors.isEmpty()) {
                if (!isAdded()) return;
                requireActivity().runOnUiThread(() ->
                        Toast.makeText(requireContext(), "لا يوجد أطباء مضافين بعد", Toast.LENGTH_SHORT).show()
                );
                return;
            }

            List<String> names = new ArrayList<>();
            for (Doctor d : doctors) names.add(d.getName());

            if (!isAdded()) return;
            requireActivity().runOnUiThread(() -> {
                ArrayAdapter<String> doctorAdapter = new ArrayAdapter<>(
                        requireContext(), android.R.layout.simple_spinner_dropdown_item, names);
                spDoctor.setAdapter(doctorAdapter);

                // Preselect أول طبيب وتعبئة الـ slots
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

                // زر الحفظ مع تحقق وتخزين بالخلفية
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

                    Executors.newSingleThreadExecutor().execute(() -> {
                        MyDataBase db2 = MyDataBase.getDatabase(requireContext());

                        // تحقق المستخدم
                        User u = db2.userDao().getById(userId);
                        if (u == null) {
                            if (!isAdded()) return;
                            requireActivity().runOnUiThread(() -> {
                                Toast.makeText(requireContext(), "جلستك غير صالحة، سجّل الدخول مجددًا", Toast.LENGTH_LONG).show();
                                requireActivity().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
                                        .edit().clear().apply();
                            });
                            return;
                        }

                        // تحقق الطبيب
                        Doctor real = db2.doctorDao().getById(selected.getId());
                        if (real == null) {
                            if (!isAdded()) return;
                            requireActivity().runOnUiThread(() ->
                                    Toast.makeText(requireContext(), "الطبيب المحدد غير موجود", Toast.LENGTH_SHORT).show()
                            );
                            return;
                        }

                        // تعارض الوقت
                        Appointment conflict = db2.appointmentDao()
                                .getAppointmentByDoctorAndTime(selected.getId(), slot);
                        if (conflict != null) {
                            if (!isAdded()) return;
                            requireActivity().runOnUiThread(() ->
                                    Toast.makeText(requireContext(), "هذا الوقت محجوز لهذا الطبيب", Toast.LENGTH_SHORT).show()
                            );
                            return;
                        }

                        // الإدراج
                        Appointment a = new Appointment();
                        a.setUserId(userId);
                        a.setDoctorId(selected.getId());
                        a.setSlot(slot);
                        a.setStatus("Pending");
                        db2.appointmentDao().insert(a);

                        if (!isAdded()) return;
                        requireActivity().runOnUiThread(() -> {
                            dialog.dismiss();
                            showNotification("تمت الإضافة", "تم حفظ الموعد بنجاح ✅");
                        });
                    });

                });
            });
        });
    }

    private void ensureNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Appointments Notifications",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            channel.setDescription("Used for showing appointment confirmations and reminders");
            NotificationManager nm = requireContext().getSystemService(NotificationManager.class);
            nm.createNotificationChannel(channel);
        }
    }

    private void ensurePostNotificationsPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (requireContext().checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS)
                    != android.content.pm.PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{android.Manifest.permission.POST_NOTIFICATIONS}, REQ_POST_NOTI);
            }
        }
    }

    private void showNotification(String title, String message) {
        Context ctx = requireContext();

        Intent intent = new Intent(ctx, MainActivity.class); // غيّر الوجهة إن أردت
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                ctx,
                0,
                intent,
                Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ? PendingIntent.FLAG_IMMUTABLE : 0
        );

        NotificationCompat.Builder builder = new NotificationCompat.Builder(ctx, CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher) // استخدم أيقونة إشعار مناسبة لديك
                .setContentTitle(title)
                .setContentText(message)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat.from(ctx)
                .notify((int) System.currentTimeMillis(), builder.build());
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
        if (binding.emptyState != null) {
            binding.emptyState.setVisibility(empty ? View.VISIBLE : View.GONE);
        } else if (binding.ivEmpty != null) {
            binding.ivEmpty.setVisibility(empty ? View.VISIBLE : View.GONE);
        }
        binding.rvAppointments.setVisibility(empty ? View.GONE : View.VISIBLE);
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
