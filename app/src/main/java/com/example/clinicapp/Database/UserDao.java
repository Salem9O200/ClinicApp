package com.example.clinicapp.Database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.clinicapp.Model.User;

import java.util.List;

// UserDao.java
@Dao
public interface UserDao {
    @Insert
    long insert(User user);  // <-- بدل void إلى long لإرجاع id

    @Query("SELECT * FROM users WHERE email = :email")
    List<User> getUserByEmail(String email);

    @Query("SELECT * FROM users WHERE email = :email AND password = :password LIMIT 1")
    User login(String email, String password);

    @Query("SELECT * FROM users WHERE id = :id LIMIT 1")
    User getById(int id);
    @Query("UPDATE users SET name = :name, email = :email WHERE id = :id")
    int updateBasic(int id, String name, String email);

    @Query("UPDATE users SET password = :password WHERE id = :id")
    int updatePassword(int id, String password);

    @Query("SELECT COUNT(*) FROM users")
    int countUsers();
}

