package com.example.clinicapp.Database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.clinicapp.Model.Appointment;

import java.util.List;

@Dao
public interface AppointmentDao {
    @Insert
    long insert(Appointment appointment);

    @Query("SELECT * FROM appointments WHERE userId = :userId ORDER BY dateTime DESC")
    List<Appointment> getAppointmentsByUser(int userId);

    @Query("SELECT * FROM appointments WHERE id = :id")
    Appointment getAppointmentById(int id);

    @Query("SELECT * FROM appointments WHERE doctorId = :doctorId AND dateTime = :dateTime")
    Appointment getAppointmentByDoctorAndTime(int doctorId, String dateTime);
}