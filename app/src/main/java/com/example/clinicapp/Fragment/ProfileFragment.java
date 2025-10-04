package com.example.clinicapp.Fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.example.clinicapp.AuthActivity;
import com.example.clinicapp.Database.MyDataBase;
import com.example.clinicapp.Model.User;
import com.example.clinicapp.R;
import com.example.clinicapp.databinding.FragmentProfileBinding;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;

import java.util.concurrent.Executors;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;
    private int userId = -1;
    private User currentUser;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);

        binding.btnEdit.setOnClickListener(v -> showEditDialog());
        binding.btnLogout.setOnClickListener(v -> logout());

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        SharedPreferences prefs = requireActivity().getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
        userId = prefs.getInt("userId", -1);

        if (userId == -1) {
            Toast.makeText(requireContext(), "يرجى تسجيل الدخول", Toast.LENGTH_SHORT).show();
            return;
        }
        loadUser();
    }

    private void loadUser() {
        Executors.newSingleThreadExecutor().execute(() -> {
            MyDataBase db = MyDataBase.getDatabase(requireContext());
            currentUser = db.userDao().getById(userId);
            if (!isAdded()) return;
            requireActivity().runOnUiThread(() -> {
                if (currentUser != null) {
                    binding.tvName.setText("الاسم: " + currentUser.getName());
                    binding.tvEmail.setText("البريد: " + currentUser.getEmail());
                }
            });
        });
    }

    private void showEditDialog() {
        if (currentUser == null) {
            Toast.makeText(requireContext(), "لا يوجد مستخدم", Toast.LENGTH_SHORT).show();
            return;
        }

        View v = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_edit_profile, null, false);
        TextInputEditText etName = v.findViewById(R.id.etName);
        TextInputEditText etEmail = v.findViewById(R.id.etEmail);
        TextInputEditText etPassword = v.findViewById(R.id.etPassword);

        etName.setText(currentUser.getName());
        etEmail.setText(currentUser.getEmail());

        AlertDialog dialog = new MaterialAlertDialogBuilder(requireContext())
                .setTitle("تعديل البيانات")
                .setView(v)
                .setNegativeButton("إلغاء", (d, w) -> d.dismiss())
                .setPositiveButton("حفظ", null)
                .create();

        dialog.show();

        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(btn -> {
            String name = etName.getText() == null ? "" : etName.getText().toString().trim();
            String email = etEmail.getText() == null ? "" : etEmail.getText().toString().trim();
            String password = etPassword.getText() == null ? "" : etPassword.getText().toString();

            if (TextUtils.isEmpty(name)) { etName.setError("الاسم مطلوب"); return; }
            if (TextUtils.isEmpty(email)) { etEmail.setError("البريد مطلوب"); return; }

            Executors.newSingleThreadExecutor().execute(() -> {
                MyDataBase db = MyDataBase.getDatabase(requireContext());
                db.userDao().updateBasic(userId, name, email);
                if (!TextUtils.isEmpty(password)) {
                    db.userDao().updatePassword(userId, password);
                }
                currentUser = db.userDao().getById(userId);

                if (!isAdded()) return;
                requireActivity().runOnUiThread(() -> {
                    Toast.makeText(requireContext(), "تم التحديث بنجاح", Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                    // حدّث الواجهة
                    if (currentUser != null) {
                        binding.tvName.setText("الاسم: " + currentUser.getName());
                        binding.tvEmail.setText("البريد: " + currentUser.getEmail());
                    }
                });
            });
        });
    }

    private void logout() {
        // امسح الجلسة
        requireActivity().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
                .edit()
                .clear()
                .apply();

        startActivity(new Intent(getContext(), AuthActivity.class));
        requireActivity().finish();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
