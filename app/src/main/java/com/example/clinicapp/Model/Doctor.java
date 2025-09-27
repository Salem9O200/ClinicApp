package com.example.clinicapp.Model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "doctors")
public class Doctor {
    @PrimaryKey(autoGenerate = true)
    public int id;
    public String name;
    public String specialty; // "General", "Dental", "Dermatology", "Pediatrics"
    public String imageUrl;  // مسار الصورة (يمكن أن يكون اسم ملف محلي)
    public String availableSlots; // "09:00,10:00,11:00"

    public Doctor(String name, String specialty, String imageUrl, String availableSlots) {
        this.name = name;
        this.specialty = specialty;
        this.imageUrl = imageUrl;
        this.availableSlots = availableSlots;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSpecialty() {
        return specialty;
    }

    public void setSpecialty(String specialty) {
        this.specialty = specialty;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getAvailableSlots() {
        return availableSlots;
    }

    public void setAvailableSlots(String availableSlots) {
        this.availableSlots = availableSlots;
    }
}