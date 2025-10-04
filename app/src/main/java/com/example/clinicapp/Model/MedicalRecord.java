package com.example.clinicapp.Model;

import static androidx.room.ForeignKey.CASCADE;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "records",
        foreignKeys = {
                @ForeignKey(entity = User.class, parentColumns = "id", childColumns = "userId", onDelete = CASCADE),
                @ForeignKey(entity = Doctor.class, parentColumns = "id", childColumns = "doctorId", onDelete = CASCADE)
        },
        indices = {@Index("userId"), @Index("doctorId")}
)
public class MedicalRecord {
    @PrimaryKey(autoGenerate = true) public int id;
    public int userId;
    public int doctorId;

    @NonNull public String title = "";  // مثال: "فحص أسنان"
    public String notes;                // نص حر
    public long createdAt = System.currentTimeMillis();

    // Getters / Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public int getDoctorId() { return doctorId; }
    public void setDoctorId(int doctorId) { this.doctorId = doctorId; }

    @NonNull public String getTitle() { return title; }
    public void setTitle(@NonNull String title) { this.title = title; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }
}
