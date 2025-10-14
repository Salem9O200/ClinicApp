package com.example.clinicapp;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.example.clinicapp.Database.SeedData;
import com.example.clinicapp.Fragment.AppointmentsFragment;
import com.example.clinicapp.Fragment.DoctorsFragment;
import com.example.clinicapp.Fragment.ProfileFragment;
import com.example.clinicapp.Fragment.RecordsFragment;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.navigation.NavigationView;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        applySavedLocale(); // طبّق اللغة المختارة قبل setContentView
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // بيانات تجريبية مرة واحدة
        SeedData.prepopulate(getApplicationContext());

        // قناة الإشعارات
        ensureNotificationChannel();

        // Toolbar + Drawer
        drawerLayout = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close
        );
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // (اختياري) تعبئة هيدر باسم/إيميل
        View header = navigationView.getHeaderView(0);
        if (header != null) {
            TextView tvName = header.findViewById(R.id.tvUserName);
            TextView tvEmail = header.findViewById(R.id.tvUserEmail);
            SharedPreferences prefs = getSharedPreferences("app_prefs", MODE_PRIVATE);
            if (tvName != null)  tvName.setText(prefs.getString("userName", "User"));
            if (tvEmail != null) tvEmail.setText(prefs.getString("userEmail", "user@example.com"));
        }

        // الاستماع لعناصر الـ Drawer
        navigationView.setNavigationItemSelectedListener(this::onDrawerItemSelected);

        // BottomNavigation
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
        // شاشة البداية
        bottomNav.setSelectedItemId(R.id.nav_doctors);
    }

    /** الاستماع لعناصر الـ Drawer */
    private boolean onDrawerItemSelected(MenuItem item) {
        drawerLayout.closeDrawers();
        int id = item.getItemId();

        // لو بدك تنقّل من الـDrawer كمان، فعّل هذي (وتأكد تضيف العناصر في drawer_menu.xml):
        /*
        if (id == R.id.nav_doctors) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragmentContainer, new DoctorsFragment())
                    .commit();
            return true;
        } else if (id == R.id.nav_appointments) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragmentContainer, new AppointmentsFragment())
                    .commit();
            return true;
        } else if (id == R.id.nav_records) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragmentContainer, new RecordsFragment())
                    .commit();
            return true;
        }
        */

        if (id == R.id.nav_change_language) {
            showLanguageDialog();
            return true;

        } else if (id == R.id.nav_logout) {
            doLogout();
            return true;
        }

        return false;
    }

    /** قناة إشعارات عامة للتطبيق */
    private void ensureNotificationChannel() {
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

    /* ---------- اللغة ---------- */

    private void showLanguageDialog() {
        final String[] langs = {"العربية", "English"};
        final String[] codes = {"ar", "en"};

        new MaterialAlertDialogBuilder(this)
                .setTitle("اختر اللغة")
                .setItems(langs, (d, which) -> {
                    String code = codes[which];
                    saveLocale(code);
                    applyLocale(code);
                    recreate(); // إعادة تحميل النشاط لتطبيق اللغة
                })
                .setNegativeButton("إلغاء", null)
                .show();
    }

    private void saveLocale(String code) {
        getSharedPreferences("app_prefs", MODE_PRIVATE)
                .edit().putString("app_locale", code).apply();
    }

    private void applySavedLocale() {
        String code = getSharedPreferences("app_prefs", MODE_PRIVATE)
                .getString("app_locale", null);
        if (code != null) applyLocale(code);
    }

    private void applyLocale(String code) {
        Locale locale = new Locale(code);
        Locale.setDefault(locale);
        Configuration config = new Configuration(getResources().getConfiguration());
        config.setLocale(locale);
        getResources().updateConfiguration(config, getResources().getDisplayMetrics());
    }

    /* ---------- تسجيل الخروج ---------- */

    private void doLogout() {
        getSharedPreferences("app_prefs", MODE_PRIVATE).edit().clear().apply();
        startActivity(new Intent(this, AuthActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
    }
}
