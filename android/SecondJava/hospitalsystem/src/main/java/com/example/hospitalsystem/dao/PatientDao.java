package com.example.hospitalsystem.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.hospitalsystem.entity.Patient;

@Dao
public interface PatientDao {

    @Insert
    void insert(Patient patient);

    @Query("SELECT * FROM Patient WHERE userId = :id")
    Patient searchPatientById(String id);

    @Update
    void update(Patient patient);

    @Delete
    void delete(Patient patient);

}
