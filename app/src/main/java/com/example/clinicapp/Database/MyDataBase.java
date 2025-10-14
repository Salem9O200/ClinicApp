package com.example.clinicapp.Database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.clinicapp.Model.Appointment;
import com.example.clinicapp.Model.Doctor;
import com.example.clinicapp.Model.MedicalRecord;
import com.example.clinicapp.Model.User;

@Database(entities = {User.class, Doctor.class, Appointment.class, MedicalRecord.class}, version = 5, exportSchema = false)
public abstract class MyDataBase extends RoomDatabase {
    public abstract UserDao userDao();
    public abstract DoctorDao doctorDao();
    public abstract AppointmentDao appointmentDao();
    public abstract MedicalRecordDao medicalRecordDao();

    private static volatile MyDataBase INSTANCE;

    public static MyDataBase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (MyDataBase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(), MyDataBase.class, "clinic_db")
                            .fallbackToDestructiveMigration()
                            .addCallback(new Callback() {
                                @Override
                                public void onCreate(@androidx.annotation.NonNull SupportSQLiteDatabase db) {
                                    SeedData.prepopulate(context.getApplicationContext());
                                }
                            })
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
