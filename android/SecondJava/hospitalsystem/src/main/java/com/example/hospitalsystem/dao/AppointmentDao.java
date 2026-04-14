package com.example.hospitalsystem.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.hospitalsystem.entity.Appointment;

import java.util.Date;
import java.util.List;

@Dao
public interface AppointmentDao {
    @Insert
    long insert(Appointment appointment);

    // 根据排班记录id查询预约总数
    @Query("SELECT * FROM appointments WHERE schedule_id = :schedule_id")
    List<Appointment> queryAppointNumByScheduleId(int schedule_id);

    // 根据排班记录id和患者用户id
    @Query("SELECT * FROM appointments WHERE schedule_id = :schedule_id AND patient_id = :user_name")
    Appointment queryAppointmentByScheduleIdAndPatientId(int schedule_id, String user_name);

    // 根据患者用户id查询预约记录
    @Query("SELECT * FROM appointments WHERE patient_id = :user_name")
    List<Appointment> queryAppointmentByPatientId(String user_name);

    @Update
    void update(Appointment appointment);

    // 根据患者id、医生姓名、就诊日期和科室id查询预约记录
    @Query("SELECT * FROM appointments WHERE visit_date = :date AND patient_id = :user_name AND doctor_id IN" +
            " (SELECT doctor_id FROM doctor_infos WHERE (office_id = :office_id OR :office_id = -1) AND name LIKE '%' || :doctor_name || '%')")
    List<Appointment> queryAppointmentByPatientIdAndDoctorNameAndOfficeIdAndDate(Date date, String user_name, int office_id, String doctor_name);

    // 根据患者id、医生姓名和科室id查询预约记录
    @Query("SELECT * FROM appointments WHERE patient_id = :user_name AND doctor_id IN" +
            " (SELECT doctor_id FROM doctor_infos WHERE (office_id = :office_id OR :office_id = -1) AND name LIKE '%' || :doctor_name || '%')")
    List<Appointment> queryAppointmentByPatientIdAndDoctorNameAndOfficeId(String user_name, int office_id, String doctor_name);

    // 清空记录
    @Query("DELETE FROM appointments")
    void clear();

}
