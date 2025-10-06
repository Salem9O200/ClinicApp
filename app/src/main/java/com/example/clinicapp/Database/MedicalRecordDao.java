package com.example.clinicapp.Database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.clinicapp.Model.MedicalRecord;

import java.util.List;

@Dao
public interface MedicalRecordDao {

    @Insert
    long insert(MedicalRecord record);

    @Delete
    int delete(MedicalRecord record);            // ✅ حذف صحيح حسب الكيان

    @Query("DELETE FROM records WHERE id = :id")
    int deleteById(long id);

    @Query("DELETE FROM records WHERE userId = :userId")
    int deleteByUser(int userId);

    @Query("SELECT * FROM records WHERE userId = :uid ORDER BY createdAt DESC")
    LiveData<List<MedicalRecord>> getByUserLive(int uid);

    @Query("SELECT COUNT(*) FROM records")
    int count();

    @Query("SELECT * FROM records WHERE id = :id LIMIT 1")
    MedicalRecord getById(int id);
}
