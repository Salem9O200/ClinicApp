package com.example.clinicapp.Database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.clinicapp.Model.Doctor;

import java.util.List;

@Dao
public interface DoctorDao {
    @Insert
    void insert(Doctor doctor);

    @Query("SELECT * FROM doctors")
    List<Doctor> getAllDoctors();

    @Query("SELECT * FROM doctors WHERE id = :id")
    Doctor getDoctorById(int id);

    @Query("SELECT * FROM doctors WHERE specialty = :specialty")
    List<Doctor> getDoctorsBySpecialty(String specialty);
}