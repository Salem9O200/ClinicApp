package com.example.clinicapp.Database;

import android.content.Context;

import com.example.clinicapp.Model.Appointment;
import com.example.clinicapp.Model.Doctor;
import com.example.clinicapp.Model.User;

import java.util.concurrent.Executors;

public class SeedData {

    public static void prepopulate(Context ctx) {
        Executors.newSingleThreadExecutor().execute(() -> {
            MyDataBase db = MyDataBase.getDatabase(ctx);

            try {
                // -------- Doctors --------
                long dGeneralId = -1, dDentalId = -1, dDermaId = -1, dPediaId = -1;

                if (db.doctorDao().count() == 0) {
                    db.doctorDao().insert(new Doctor(
                            "Dr. Ahmad Khalil", "General", "doctor1", "09:00,10:00,11:00", "111111111"));
                    db.doctorDao().insert(new Doctor(
                            "Dr. Sami Ahmad", "General", "doctor1", "08:00,10:00,12:00", "1212121212"));

                    db.doctorDao().insert(new Doctor(
                            "Dr. Lina Youssef", "Dental", "doctor2", "11:00,12:00,13:00", "222222222"));
                    db.doctorDao().insert(new Doctor(
                            "Dr. Ali Khaled", "Dental", "doctor2", "13:00,15:00,17:00", "2424242424"));

                    db.doctorDao().insert(new Doctor(
                            "Dr. Sami Omar", "Dermatology", "doctor3", "14:00,15:00", "333333333"));
                    db.doctorDao().insert(new Doctor(
                            "Dr. Ahmad Okale", "Dermatology", "doctor3", "10:00,13:00", "3535353535"));

                    db.doctorDao().insert(new Doctor(
                            "Dr. Leila Abdullah", "Pediatrics", "doctor4", "09:00,13:00,15:00", "444444444"));
                 db.doctorDao().insert(new Doctor(
                            "Dr. Sham Ziad", "Pediatrics", "doctor4", "08:00,10:00,12:00", "4646464646"));
                }
                else {
                    // اختيار أي دكتور موجود كاحتياط (لن نستخدمه غالبًا)
                    Doctor first = db.doctorDao().getFirstDoctor();
                    if (first != null) {
                        dGeneralId = first.getId();
                        dDentalId  = first.getId();
                        dDermaId   = first.getId();
                        dPediaId   = first.getId();
                    }
                }

                // -------- User --------
                long userId;
                if (db.userDao().countUsers() == 0) {
                    // تذكّر: Register/Login لازم يستخدم نفس الباسوورد
                    userId = db.userDao().insert(new User("Test User", "test@example.com", "1234"));
                } else {
                    User existing = null;
                    for (User u : db.userDao().getUserByEmail("test@example.com")) {
                        existing = u; break;
                    }
                    if (existing == null) {
                        userId = db.userDao().insert(new User("Test User", "test@example.com", "1234"));
                    } else {
                        userId = existing.id;
                    }
                }

                // -------- Appointments (اختياري للتجربة) --------
                if (db.appointmentDao().countAppointments() == 0) {
                    // استخدم الـ ids الحقيقية لو كانت متوفرة
                    int generalId = dGeneralId > 0 ? (int) dGeneralId : 1;
                    int dentalId = dDentalId > 0 ? (int) dDentalId : 2;
                    int dermaId  = dDermaId  > 0 ? (int) dDermaId  : 3;
                    int pediaId = dPediaId > 0 ? (int) dPediaId : 4;

                    Appointment a1 = new Appointment();
                    a1.setUserId((int) userId);
                    a1.setDoctorId(dentalId);
                    a1.setSlot("2025-10-02 11:00");
                    a1.setStatus("Pending");
                    db.appointmentDao().insert(a1);

                    Appointment a2 = new Appointment();
                    a2.setUserId((int) userId);
                    a2.setDoctorId(dermaId);
                    a2.setSlot("2025-10-03 15:00");
                    a2.setStatus("Completed");
                    db.appointmentDao().insert(a2);
                }

            } catch (Exception ignored) { /* تجاهل أي استثناء أثناء التحضير */ }
        });
    }
}
