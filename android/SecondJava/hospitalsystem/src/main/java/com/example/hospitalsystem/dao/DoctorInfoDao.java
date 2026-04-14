package com.example.hospitalsystem.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.hospitalsystem.entity.DoctorInfo;

import java.util.List;

@Dao
public interface DoctorInfoDao {

    @Insert
    void insert(DoctorInfo doctorInfo);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertDoctorInfos(List<DoctorInfo> list);

    @Query("DELETE FROM doctor_infos")
    void clear();

    @Query("SELECT * FROM doctor_infos WHERE doctor_id = :id")
    DoctorInfo queryByDoctorId(String id);

    // 根据姓名部分匹配医生
    @Query("SELECT * FROM doctor_infos WHERE name LIKE '%' || :name || '%'")
    List<DoctorInfo> queryDoctorByName(String name);

    @Query("SELECT * FROM doctor_infos")
    List<DoctorInfo> queryAll();

    // 根据医生姓名和科室id查询
    @Query("SELECT * FROM doctor_infos WHERE name LIKE '%' || :doctor_name || '%' " +
            "AND (office_id = :office_id OR :office_id = -1)")
    List<DoctorInfo> queryDoctorByNameAndOfficeId(String doctor_name, int office_id);

}
