package com.example.hospitalsystem.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.hospitalsystem.entity.Office;

import java.util.List;

@Dao
public interface OfficeDao {

    @Query("SELECT * FROM Office")
    List<Office> queryAll();

    // 查询所有科室的名称
    @Query("SELECT name FROM Office")
    List<String> queryAllName();

    @Insert
    void insert(Office office);

    @Delete
    void delete(Office office);

    @Query("DELETE FROM Office")
    void clear();

    // 插入多条科室信息
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertOfficeInfos(List<Office> list);

    // 根据科室名称部分匹配查询
    @Query("SELECT * FROM Office WHERE name LIKE '%' || :name || '%'")
    List<Office> queryOfficeByName(String name);

    // 根据科室id查询科室
    @Query("SELECT * FROM Office WHERE office_id = :office_id")
    Office queryOfficeByid(int office_id);

    // 根据支付id查询科室
    @Query("SELECT * FROM Office WHERE office_id IN " +
            "(SELECT office_id FROM doctor_infos WHERE doctor_id IN " +
            "(SELECT doctor_id FROM appointments WHERE appointment_id IN " +
            "(SELECT appointment_id FROM payments WHERE payment_id = :payment_id)))")
    Office queryOfficeByPaymenyId(int payment_id);

    // 根据医生id查询科室
    @Query("SELECT * FROM Office WHERE office_id IN " +
            "(SELECT office_id FROM doctor_infos WHERE doctor_id = :doctor_id)")
    Office queryOfficeByDoctorid(String doctor_id);

}
