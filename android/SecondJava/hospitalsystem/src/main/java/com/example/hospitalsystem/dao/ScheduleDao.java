package com.example.hospitalsystem.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.hospitalsystem.entity.Schedule;

import java.util.Date;
import java.util.List;

@Dao
public interface ScheduleDao {

    @Insert
    void insert(Schedule schedule);

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertScheduleInfos(List<Schedule> list);

    @Query("DELETE FROM Schedule")
    void clear();

    // 根据科室名称查询排班信息 (部分匹配)
    @Query("SELECT * FROM Schedule WHERE office_id IN" +
            " (SELECT office_id FROM Office WHERE name LIKE '%' || :name || '%')")
    List<Schedule> queryScheduleByOfficeName(String name);

    // 根据医生姓名和排班日期查询排班信息 (部分匹配)
    @Query("SELECT * FROM Schedule WHERE office_id = :office_id AND schedule_date = :date AND doctor_id IN " +
            "(SELECT doctor_id FROM doctor_infos WHERE name  LIKE '%' || :name || '%')")
    List<Schedule> queryScheduleByDoctorNameAndDate(Date date, String name, int office_id);

    // 根据医生姓名和排班日期查询排班信息 (部分匹配)
    @Query("SELECT * FROM Schedule WHERE office_id = :office_id AND doctor_id IN " +
            "(SELECT doctor_id FROM doctor_infos WHERE name  LIKE '%' || :name || '%')")
    List<Schedule> queryScheduleByDoctorName(String name, int office_id);

    // 根据医生id、日期和时段查询排班信息
    @Query("SELECT * FROM Schedule WHERE doctor_id = :doctor_id AND schedule_date = :date " +
            "AND (schedule_period = :period OR :period = 0)")
    List<Schedule> queryScheduleByDoctorIdAndDateAndPeriod(String doctor_id, Date date, int period);

    // 根据医生id和时段查询排班信息
    @Query("SELECT * FROM Schedule WHERE doctor_id = :doctor_id " +
            "AND (schedule_period = :period OR :period = 0)")
    List<Schedule> queryScheduleByDoctorIdAndPeriod(String doctor_id, int period);

    // 根据医生id查询排班信息
    @Query("SELECT * FROM Schedule WHERE doctor_id = :doctor_id")
    List<Schedule> queryScheduleByDoctorId(String doctor_id);
}
