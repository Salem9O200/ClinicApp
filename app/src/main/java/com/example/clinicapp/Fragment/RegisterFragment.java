package com.example.clinicapp.Fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.clinicapp.Database.MyDataBase;
import com.example.clinicapp.MainActivity;
import com.example.clinicapp.Model.User;
import com.example.clinicapp.databinding.FragmentRegisterBinding;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RegisterFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RegisterFragment extends Fragment {
    FragmentRegisterBinding binding;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public RegisterFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RegesterFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RegisterFragment newInstance(String param1, String param2) {
        RegisterFragment fragment = new RegisterFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentRegisterBinding.inflate(inflater,container,false);
        binding.btnRegister.setOnClickListener(v -> register());
        // Inflate the layout for this fragment
        return binding.getRoot();
    }
    private void register() {
        String name = binding.etName.getText().toString().trim();
        String email = binding.etEmail.getText().toString().trim();
        String phone = binding.etPhone.getText().toString().trim();
        String password = binding.etPassword.getText().toString().trim();

        if (name.isEmpty() || email.isEmpty() || phone.isEmpty() || password.isEmpty()) {
            Toast.makeText(getContext(), "يرجى ملء جميع الحقول", Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.length() < 6) {
            Toast.makeText(getContext(), "كلمة المرور يجب أن تكون 6 أحرف على الأقل", Toast.LENGTH_SHORT).show();
            return;
        }

        // ✅ نفّذ كل شيء في خيط خلفي
        new Thread(() -> {
            MyDataBase db = MyDataBase.getDatabase(requireContext());

            // التحقق من البريد
            boolean emailExists = !db.userDao().getUserByEmail(email).isEmpty();

            if (emailExists) {
                // العودة للخيط الرئيسي لعرض Toast
                requireActivity().runOnUiThread(() -> {
                    Toast.makeText(getContext(), "هذا البريد الإلكتروني مستخدم مسبقًا", Toast.LENGTH_SHORT).show();
                });
                return;
            }

            // إنشاء مستخدم جديد
            User newUser = new User(name, email, password, phone);
            db.userDao().insert(newUser);

            // حفظ الجلسة والانتقال
            requireActivity().runOnUiThread(() -> {
                requireActivity().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
                        .edit()
                        .putBoolean("isLoggedIn", true)
                        .putInt("userId", newUser.id)
                        .apply();

                Toast.makeText(getContext(), "تم إنشاء الحساب بنجاح!", Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(requireActivity(), MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                requireActivity().finish();
            });
        }).start();
    }
}