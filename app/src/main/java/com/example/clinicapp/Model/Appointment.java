package com.example.clinicapp.Model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "appointments")
public class Appointment {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public int userId;      // معرف المستخدم الذي حجز الموعد
    public int doctorId;    // معرف الطبيب
    public String dateTime; // التاريخ والوقت: "2025-04-05 10:00"
    public String status;   // "Upcoming", "Completed", "Cancelled"

    public Appointment(int userId, int doctorId, String dateTime, String status) {
        this.userId = userId;
        this.doctorId = doctorId;
        this.dateTime = dateTime;
        this.status = status;
    }

    // Getters و Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public int getDoctorId() { return doctorId; }
    public void setDoctorId(int doctorId) { this.doctorId = doctorId; }

    public String getDateTime() { return dateTime; }
    public void setDateTime(String dateTime) { this.dateTime = dateTime; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}