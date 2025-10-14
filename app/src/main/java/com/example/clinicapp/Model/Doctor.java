package com.example.clinicapp.Model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "doctors")
public class Doctor {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String name;
    private String category;
    private int imageRes;
    private String slotsCsv;
    private String phone;

    public Doctor(String name, String category, int imageRes, String slotsCsv, String phone) {
        this.name = name;
        this.category = category;
        this.imageRes = imageRes;
        this.slotsCsv = slotsCsv;
        this.phone = phone;
    }

    // Getters & Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public int getImageRes() { return imageRes; }
    public void setImageRes(int imageRes) { this.imageRes = imageRes; }

    public String getSlotsCsv() { return slotsCsv; }
    public void setSlotsCsv(String slotsCsv) { this.slotsCsv = slotsCsv; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
}
