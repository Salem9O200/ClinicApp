package com.example.clinicapp.Model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "doctors")
public class Doctor {
    @PrimaryKey(autoGenerate = true) private int id;
    private String name;
    private String category;
    private String imageUrl;
    private String slotsCsv;
    private String phone;

    public Doctor() {}
    public Doctor(String name, String category, String imageUrl, String slotsCsv, String phone) {
        this.name = name; this.category = category; this.imageUrl = imageUrl; this.slotsCsv = slotsCsv; this.phone = phone;
    }

    public int getId() { return id; } public void setId(int id) { this.id = id; }
    public String getName() { return name; } public void setName(String name) { this.name = name; }
    public String getCategory() { return category; } public void setCategory(String category) { this.category = category; }
    public String getImageUrl() { return imageUrl; } public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public String getSlotsCsv() { return slotsCsv; } public void setSlotsCsv(String slotsCsv) { this.slotsCsv = slotsCsv; }
    public String getPhone() { return phone; } public void setPhone(String phone) { this.phone = phone; }
}
