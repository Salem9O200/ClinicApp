package com.example.clinicapp.Database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.clinicapp.Model.Doctor;

import java.util.List;

@Dao
public interface DoctorDao {
    @Insert
    long insert(Doctor doctor);

    @Insert
    void insertAll(List<Doctor> doctors);

    @Query("SELECT * FROM doctors")
    List<Doctor> getAllSync();

    @Query("SELECT * FROM doctors WHERE id = :id LIMIT 1")
    Doctor getById(int id);

    @Query("SELECT * FROM doctors ORDER BY id ASC LIMIT 1")
    Doctor getFirstDoctor();

    @Query("SELECT * FROM doctors WHERE category = :category")
    LiveData<List<Doctor>> getByCategory(String category);

    // 🔽 جديد:
    @Query("SELECT id FROM doctors WHERE name = :name LIMIT 1")
    Integer getIdByName(String name);

    @Query("SELECT * FROM doctors WHERE category = :category")
    List<Doctor> getByCategorySync(String category);


    @Query("SELECT COUNT(*) FROM doctors")
    int count();
}
