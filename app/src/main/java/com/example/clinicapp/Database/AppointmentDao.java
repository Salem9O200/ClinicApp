package com.example.clinicapp.Database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.clinicapp.Model.Appointment;

import java.util.List;

@Dao
public interface AppointmentDao {

    @Insert
    long insert(Appointment appointment);

    // لعرض المواعيد الخاصة بالمستخدم (LiveData)
    @Query("SELECT * FROM appointments WHERE userId = :uid ORDER BY createdAt DESC")
    LiveData<List<Appointment>> getAppointmentsByUserLive(int uid);

    // خيار إضافي (بدون LiveData) - مفيد للاختبارات أو المهام الخلفية
    @Query("SELECT * FROM appointments WHERE userId = :uid ORDER BY createdAt DESC")
    List<Appointment> getAppointmentsByUser(int uid);

    // إحضار موعد واحد (لو احتجته)
    @Query("SELECT * FROM appointments WHERE id = :id LIMIT 1")
    Appointment getAppointmentById(int id);

    // للتحقق من التعارض — نعتمد slot كوقت الحجز
    @Query("SELECT * FROM appointments WHERE doctorId = :doctorId AND slot = :slot LIMIT 1")
    Appointment getAppointmentByDoctorAndTime(int doctorId, String slot);

    @Query("SELECT COUNT(*) FROM appointments")
    int countAppointments();
}
