package com.example.clinicapp.Database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.clinicapp.Model.Appointment;
import com.example.clinicapp.Model.Doctor;
import com.example.clinicapp.Model.User;


@Database(entities = {User.class , Doctor.class , Appointment.class}, version = 2, exportSchema = false)
public abstract class MyDataBase extends RoomDatabase {
    public abstract UserDao userDao();
    public abstract DoctorDao doctorDao();
    public abstract AppointmentDao appointmentDao();

    private static volatile MyDataBase INSTANCE;

    public static MyDataBase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (MyDataBase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    MyDataBase.class, "clinic_database")
                            .fallbackToDestructiveMigration() // ⚠️ مؤقت للتجربة فقط
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
