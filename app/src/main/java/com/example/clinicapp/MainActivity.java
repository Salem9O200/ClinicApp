package com.example.clinicapp;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.os.LocaleListCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.clinicapp.Database.SeedData;
import com.example.clinicapp.Fragment.AppointmentsFragment;
import com.example.clinicapp.Fragment.DoctorsFragment;
import com.example.clinicapp.Fragment.ProfileFragment;
import com.example.clinicapp.Fragment.RecordsFragment;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity {

    private static final String PREFS = "app_prefs";
    private static final String KEY_APP_LOCALE = "app_locale";

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private BottomNavigationView bottomNav;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        // 1) طبّق اللغة المحفوظة قبل رسم أي واجهة
        String savedLang = getSavedLanguage("ar");
        AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(savedLang));

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 2) بيانات تجريبية
        SeedData.prepopulate(getApplicationContext());

        // 3) قناة إشعارات
        ensureNotificationChannel();

        // 4) Toolbar + Drawer
        drawerLayout   = findViewById(R.id.drawerLayout);
        navigationView = findViewById(R.id.navigationView);
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close
        );
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // 5) تعبئة هيدر الـDrawer
        View header = navigationView.getHeaderView(0);
        if (header != null) {
            TextView tvName  = header.findViewById(R.id.tvUserName);
            TextView tvEmail = header.findViewById(R.id.tvUserEmail);
            SharedPreferences prefs = getSharedPreferences(PREFS, MODE_PRIVATE);
            if (tvName  != null) tvName.setText(prefs.getString("userName",  "User"));
            if (tvEmail != null) tvEmail.setText(prefs.getString("userEmail", "user@example.com"));
        }

        // 6) الاستماع لعناصر الـDrawer
        navigationView.setNavigationItemSelectedListener(this::onDrawerItemSelected);

        // 7) BottomNavigation
        bottomNav = findViewById(R.id.bottomNav);
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

        // 8) شاشة البداية
        bottomNav.setSelectedItemId(R.id.nav_doctors);
    }

    /** عناصر الـ Drawer */
    private boolean onDrawerItemSelected(MenuItem item) {
        drawerLayout.closeDrawers();
        int id = item.getItemId();

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
                .setTitle(getString(R.string.change_language))
                .setItems(langs, (d, which) -> changeLanguage(codes[which]))
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    private void changeLanguage(String langTag) {
        // خزّن اللغة
        saveLanguage(langTag);

        // طبّق اللغة على مستوى التطبيق
        AppCompatDelegate.setApplicationLocales(LocaleListCompat.forLanguageTags(langTag));

        // فضي الـBackStack للFragments حتى ما تبقى نسخ قديمة بالنصوص
        FragmentManager fm = getSupportFragmentManager();
        for (int i = 0; i < fm.getBackStackEntryCount(); i++) {
            fm.popBackStackImmediate();
        }

        // أعد تهيئة النشاط بالكامل لتتحدّث كل الواجهات
        recreate();
    }

    private void saveLanguage(String code) {
        getSharedPreferences(PREFS, MODE_PRIVATE)
                .edit()
                .putString(KEY_APP_LOCALE, code)
                .apply();
    }

    private String getSavedLanguage(String def) {
        return getSharedPreferences(PREFS, MODE_PRIVATE)
                .getString(KEY_APP_LOCALE, def);
    }

    /* ---------- تسجيل الخروج ---------- */

    private void doLogout() {
        getSharedPreferences(PREFS, MODE_PRIVATE).edit().clear().apply();
        startActivity(new Intent(this, AuthActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
    }
}
