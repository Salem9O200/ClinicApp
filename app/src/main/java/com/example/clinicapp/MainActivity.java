package com.example.clinicapp;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.clinicapp.Database.MyDataBase;
import com.example.clinicapp.Database.SeedData;
import com.example.clinicapp.Fragment.AppointmentsFragment;
import com.example.clinicapp.Fragment.DoctorsFragment;
import com.example.clinicapp.Fragment.ProfileFragment;
import com.example.clinicapp.Fragment.RecordsFragment;
import com.example.clinicapp.Model.Doctor;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SeedData.prepopulate(getApplicationContext());


        BottomNavigationView bottomNav = findViewById(R.id.bottomNav);
        bottomNav.setOnItemSelectedListener(item -> {
            Fragment f;
            int id = item.getItemId();

            if (id == R.id.nav_doctors) {
                f = new DoctorsFragment();
            } else if (id == R.id.nav_appointments) {
                f = new AppointmentsFragment();
            } else if (id == R.id.nav_records) {
                f = new RecordsFragment();
            } else {
                f = new ProfileFragment();
            }

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragmentContainer, f)
                    .commit();
            return true;
        });

        bottomNav.setSelectedItemId(R.id.nav_doctors);


            // إنشاء القناة
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                NotificationChannel channel = new NotificationChannel(
                        "reminder_channel",
                        "Appointments Notifications",
                        NotificationManager.IMPORTANCE_DEFAULT
                );
                channel.setDescription("Used for showing appointment reminders");

                NotificationManager manager = getSystemService(NotificationManager.class);
                manager.createNotificationChannel(channel);
            }
        }


    private void insertSampleDoctorsOnce() {
        Executors.newSingleThreadExecutor().execute(() -> {
            MyDataBase db = MyDataBase.getDatabase(getApplicationContext());
            if (db.doctorDao().count() == 0) {
                Doctor d1 = new Doctor();
                d1.setName("د. أحمد محمد");
                d1.setCategory("General");
                d1.setImageUrl("doctor1");
                d1.setSlotsCsv("09:00,10:00,11:00");
                d1.setPhone("1234567891");
                db.doctorDao().insert(d1);

                Doctor d2 = new Doctor();
                d2.setName("د. سارة خليل");
                d2.setCategory("Dental");
                d2.setImageUrl("doctor2");
                d2.setSlotsCsv("10:00,11:00,12:00");
                d2.setPhone("1234567891");
                db.doctorDao().insert(d2);

                Doctor d3 = new Doctor();
                d3.setName("د. علي حسن");
                d3.setCategory("Dermatology");
                d3.setImageUrl("doctor3");
                d3.setSlotsCsv("14:00,15:00,16:00");
                d3.setPhone("1234567891");
                db.doctorDao().insert(d3);

                Doctor d4 = new Doctor();
                d4.setName("د. ليلى عبد الله");
                d4.setCategory("Pediatrics");
                d4.setImageUrl("doctor4");
                d4.setSlotsCsv("09:00,13:00,15:00");
                d4.setPhone("1234567891");
                db.doctorDao().insert(d4);
            }
        });
    }
}
