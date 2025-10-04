package com.example.clinicapp.Model;

import static androidx.room.ForeignKey.CASCADE;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(tableName = "appointments",
        foreignKeys = {
                @ForeignKey(entity = User.class, parentColumns = "id", childColumns = "userId", onDelete = CASCADE),
                @ForeignKey(entity = Doctor.class, parentColumns = "id", childColumns = "doctorId", onDelete = CASCADE)
        },
        indices = {@Index("userId"), @Index("doctorId")}
)
public class Appointment {
    @PrimaryKey(autoGenerate = true) public int id;

    public int userId;
    public int doctorId;

    @NonNull
    public String slot = "";

    public String dateTime;

    @NonNull
    public String status = "Pending";

    public long createdAt;

    public Appointment() {
        this.createdAt = System.currentTimeMillis();
        if (this.dateTime == null) this.dateTime = this.slot;
    }

    // Getters/Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public int getDoctorId() { return doctorId; }
    public void setDoctorId(int doctorId) { this.doctorId = doctorId; }

    @NonNull
    public String getSlot() { return slot; }
    public void setSlot(@NonNull String slot) {
        this.slot = slot;
        // حافظ على التزامن مع dateTime لو بتستخدمه
        this.dateTime = slot;
    }

    public String getDateTime() { return dateTime; }
    public void setDateTime(String dateTime) { this.dateTime = dateTime; }

    @NonNull
    public String getStatus() { return status; }
    public void setStatus(@NonNull String status) { this.status = status; }

    public long getCreatedAt() { return createdAt; }
    public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }
}
