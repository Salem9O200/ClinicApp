package com.example.clinicapp.Database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.clinicapp.Model.User;

import java.util.List;

@Dao
public interface UserDao {
    @Insert
    void insert(User user);

    @Query("SELECT * FROM users WHERE email = :email")
    List<User> getUserByEmail(String email);

    @Query("SELECT * FROM users WHERE email = :email AND password = :password")
    User login(String email, String password);
}
