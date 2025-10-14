package com.example.clinicapp.Database;

import android.content.Context;

import com.example.clinicapp.Model.Appointment;
import com.example.clinicapp.Model.Doctor;
import com.example.clinicapp.Model.User;
import com.example.clinicapp.R;

import java.util.concurrent.Executors;

public class SeedData {

    private static int ensureDoctor(MyDataBase db, Doctor d) {
        Integer existingId = db.doctorDao().getIdByName(d.getName());
        if (existingId != null) return existingId;
        return (int) db.doctorDao().insert(d);
    }

    public static void prepopulate(Context ctx) {
        Executors.newSingleThreadExecutor().execute(() -> {
            MyDataBase db = MyDataBase.getDatabase(ctx);

            try {
                // -------- Doctors (insert-if-missing) --------
                int g1 = ensureDoctor(db, new Doctor("Dr. Ahmad Khalil", "General", R.drawable.doctor, "09:00,10:00,11:00", "111111111"));
                int g2 = ensureDoctor(db, new Doctor("Dr. Sami Ahmad",   "General", R.drawable.doctor, "08:00,10:00,12:00", "1212121212"));
                int g3 = ensureDoctor(db, new Doctor("Dr. Omar Saleh",   "General", R.drawable.doctor, "09:00,11:00,13:00", "555555555"));
                int g4 = ensureDoctor(db, new Doctor("Dr. Ali Saleh",   "General", R.drawable.doctor, "09:00,11:00,13:00", "555555555"));
                int g5 = ensureDoctor(db, new Doctor("Dr. Shadi Saleh",   "General", R.drawable.doctor, "09:00,11:00,13:00", "7777777777"));
                int g6 = ensureDoctor(db, new Doctor("Dr. Salah Saleh",   "General", R.drawable.doctor, "09:00,11:00,13:00", "8888888888"));

                int d1 = ensureDoctor(db, new Doctor("Dr. Lina Youssef", "Dental", R.drawable.doctor2, "11:00,12:00,13:00", "222222222"));
                int d2 = ensureDoctor(db, new Doctor("Dr. Ali Khaled",   "Dental", R.drawable.doctor, "13:00,15:00,17:00", "2424242424"));
                int d3 = ensureDoctor(db, new Doctor("Dr. Rania Fadel", "Dental",  R.drawable.doctor2, "10:00,12:00,14:00", "666666666"));
                int d4 = ensureDoctor(db, new Doctor("Dr. Salam Fadel", "Dental",  R.drawable.doctor2, "10:00,12:00,14:00", "7878787878"));
                int d5 = ensureDoctor(db, new Doctor("Dr. Hoda Fadel", "Dental",  R.drawable.doctor2, "10:00,12:00,14:00", "5959595959"));
                int d6 = ensureDoctor(db, new Doctor("Dr. Eman nia Fadel", "Dental",  R.drawable.doctor2, "10:00,12:00,14:00", "23232232323"));

                int der1 = ensureDoctor(db, new Doctor("Dr. Sami Omar",   "Dermatology", R.drawable.doctor, "14:00,15:00", "333333333"));
                int der2 = ensureDoctor(db, new Doctor("Dr. Ahmad Okale", "Dermatology", R.drawable.doctor, "10:00,13:00", "3535353535"));
                int der3 = ensureDoctor(db, new Doctor("Dr. Sali Okale", "Dermatology", R.drawable.doctor2, "10:00,13:00", "24224242424"));
                int der4 = ensureDoctor(db, new Doctor("Dr. Ahmad Okale", "Dermatology", R.drawable.doctor, "10:00,13:00", "51151151515"));
                int der5 = ensureDoctor(db, new Doctor("Dr. Hamed Okale", "Dermatology", R.drawable.doctor, "10:00,13:00", "99393939393"));
                int der6 = ensureDoctor(db, new Doctor("Dr. Jamal Okale", "Dermatology", R.drawable.doctor, "10:00,13:00", "48484848484"));

                int p1 = ensureDoctor(db, new Doctor("Dr. Leila Abdullah", "Pediatrics", R.drawable.doctor2, "09:00,13:00,15:00", "444444444"));
                int p2 = ensureDoctor(db, new Doctor("Dr. Sham Ziad",      "Pediatrics", R.drawable.doctor2, "08:00,10:00,12:00", "4646464646"));
                int p3 = ensureDoctor(db, new Doctor("Dr. Mohammed Alaa",      "Pediatrics", R.drawable.doctor, "08:00,10:00,12:00", "9999999999"));
                int p4 = ensureDoctor(db, new Doctor("Dr. Zaid Alaa",      "Pediatrics", R.drawable.doctor, "08:00,10:00,12:00", "1010101100"));
                int p5 = ensureDoctor(db, new Doctor("Dr. Ahmad Alaa",      "Pediatrics", R.drawable.doctor, "08:00,10:00,12:00", "2020202020"));
                int p6 = ensureDoctor(db, new Doctor("Dr. Sami Mansour",      "Pediatrics", R.drawable.doctor, "08:00,10:00,12:00", "3030303030"));


                // -------- User --------
                long userId;
                if (db.userDao().countUsers() == 0) {
                    userId = db.userDao().insert(new User("Test User", "test@example.com", "1234"));
                } else {
                    User existing = null;
                    for (User u : db.userDao().getUserByEmail("test@example.com")) { existing = u; break; }
                    userId = (existing == null)
                            ? db.userDao().insert(new User("Test User", "test@example.com", "1234"))
                            : existing.id;
                }

                // -------- Appointments demo --------
                if (db.appointmentDao().countAppointments() == 0) {
                    // اختَر أول طبيب من كل فئة إن وُجد
                    int dentalId = pickFirstId(db.doctorDao().getByCategorySync("Dental"), d1);
                    int dermaId  = pickFirstId(db.doctorDao().getByCategorySync("Dermatology"), der1);

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

            } catch (Exception ignored) {}
        });
    }

    private static int pickFirstId(java.util.List<Doctor> docs, int fallbackId) {
        if (docs != null && !docs.isEmpty()) return docs.get(0).getId();
        return fallbackId;
    }
}
