package com.example.clinicapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.clinicapp.Database.DoctorDao;
import com.example.clinicapp.Database.MyDataBase;
import com.example.clinicapp.Fragment.AppointmentsFragment;
import com.example.clinicapp.Fragment.DoctorsFragment;
import com.example.clinicapp.Fragment.ProfileFragment;
import com.example.clinicapp.Fragment.RecordsFragment;
import com.example.clinicapp.Model.Doctor;
import com.example.clinicapp.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        SharedPreferences prefs = getSharedPreferences("app_prefs", MODE_PRIVATE);
        if (!prefs.getBoolean("isLoggedIn", false)) {
            startActivity(new Intent(this, AuthActivity.class));
            finish();
            return;
        }

        insertSampleDoctors();

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragmentContainer, new DoctorsFragment())
                .commit();

        binding.bottomNav.setOnItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            if (item.getItemId() == R.id.navDoctors) {
                selectedFragment = new DoctorsFragment();
            } else if (item.getItemId() == R.id.navAppointments) {
                selectedFragment = new AppointmentsFragment();
            } else if (item.getItemId() == R.id.navRecords) {
                selectedFragment = new RecordsFragment();
            } else if (item.getItemId() == R.id.navProfile) {
                selectedFragment = new ProfileFragment();
            }

            if (selectedFragment != null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragmentContainer, selectedFragment)
                        .commit();
            }
            return true;
        });
    }
    private void insertSampleDoctors() {
        SharedPreferences prefs = getSharedPreferences("app_prefs", MODE_PRIVATE);
        boolean doctorsInserted = prefs.getBoolean("doctors_inserted", false);

        if (!doctorsInserted) {
            new Thread(() -> {
                MyDataBase db = MyDataBase.getDatabase(this);
                DoctorDao doctorDao = db.doctorDao();

                // إدخال الأطباء التجريبيين
                doctorDao.insert(new Doctor("د. أحمد محمد", "General", "doctor1", "09:00,10:00,11:00"));
                doctorDao.insert(new Doctor("د. سارة خليل", "Dental", "doctor2", "10:00,11:00,12:00"));
                doctorDao.insert(new Doctor("د. علي حسن", "Dermatology", "doctor3", "14:00,15:00,16:00"));
                doctorDao.insert(new Doctor("د. ليلى عبد الله", "Pediatrics", "doctor4", "09:00,13:00,15:00"));

                // تحديث SharedPreferences على الخيط الرئيسي
                runOnUiThread(() -> {
                    prefs.edit().putBoolean("doctors_inserted", true).apply();
                });
            }).start();
        }
    }
    @SuppressLint("GestureBackNavigation")
    @Override
    public void onBackPressed() {
        // يمكنك إضافة تأكيد الخروج هنا لاحقًا
        super.onBackPressed();
    }

}