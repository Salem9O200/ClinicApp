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

import com.example.clinicapp.Adapter.RecordsAdapter;
import com.example.clinicapp.Database.MyDataBase;
import com.example.clinicapp.MainActivity; // عدّلها لو الوجهة مختلفة عند الضغط على الإشعار
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

    // إعدادات الإشعارات
    private static final String CHANNEL_ID = "reminder_channel";
    private static final int REQ_POST_NOTI = 2101;

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
        binding.recyclerView.addItemDecoration(
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
                RecordsAdapter.RecordItem item = adapter.getItemAt(pos); // تأكد أن الـ Adapter يحتوي هذه الدالة
                if (item == null) { adapter.notifyItemChanged(pos); return; }

                new MaterialAlertDialogBuilder(requireContext())
                        .setTitle("تأكيد الحذف")
                        .setMessage("هل تريد حذف هذا السجل الطبي؟")
                        .setNegativeButton("إلغاء", (d, w) -> {
                            d.dismiss();
                            adapter.notifyItemChanged(pos);
                        })
                        .setPositiveButton("حذف", (d, w) -> {
                            Executors.newSingleThreadExecutor().execute(() -> {
                                int rows = MyDataBase.getDatabase(requireContext())
                                        .medicalRecordDao()
                                        .deleteById(item.record.getId());

                                requireActivity().runOnUiThread(() -> {
                                    if (rows > 0) {
                                        Toast.makeText(requireContext(), "تم حذف السجل", Toast.LENGTH_SHORT).show();
                                        showNotification("تم الحذف", "تم حذف السجل الطبي بنجاح ❌");
                                        // LiveData ستحدّث القائمة تلقائيًا
                                    } else {
                                        Toast.makeText(requireContext(), "فشل الحذف", Toast.LENGTH_SHORT).show();
                                        adapter.notifyItemChanged(pos);
                                    }
                                });
                            });
                        })
                        .show();
            }
        }).attachToRecyclerView(binding.recyclerView);

        if (binding.fabAdd != null) binding.fabAdd.setOnClickListener(v -> showAddDialog());

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
                        // صلاحية المستخدم (اختياري)
                        if (db.userDao().getById(userId) == null) {
                            if (!isAdded()) return;
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

                        if (!isAdded()) return;
                        requireActivity().runOnUiThread(() -> {
                            dialog.dismiss();
                            // إشعار بعد الإضافة
                            showNotification("تمت الإضافة", "تم حفظ السجل الطبي بنجاح ✅");
                        });
                    });
                });
            });
        });
    }

    // ====== إشعارات ======

    private void ensureNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "Appointments/Records Notifications",
                    NotificationManager.IMPORTANCE_DEFAULT
            );
            channel.setDescription("Used for confirmations and deletions of records/appointments");
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

        Intent intent = new Intent(ctx, MainActivity.class); // غيّر الوجهة إذا أردت
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

    // ====== أدوات مساعدة ======

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
