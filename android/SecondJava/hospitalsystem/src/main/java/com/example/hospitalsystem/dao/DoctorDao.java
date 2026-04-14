package com.example.hospitalsystem.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.hospitalsystem.entity.Doctor;

import java.util.List;

@Dao
public interface DoctorDao {

    @Insert
    void insert(Doctor doctor);

    // 插入多条科室信息
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertDoctors(List<Doctor> list);

    @Query("DELETE FROM Doctor")
    void clear();

}
