package com.example.hospitalsystem.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.hospitalsystem.entity.Payment;

import java.util.Date;
import java.util.List;

@Dao
public interface PaymentDao {

    @Insert
    void insert(Payment payment);

    // 根据预约记录id查询支付记录
    @Query("SELECT * FROM payments WHERE appointment_id = :appointment_id")
    Payment queryPaymentByAppointmentId(int appointment_id);

    // 根据患者id查询支付记录
    @Query("SELECT * FROM payments WHERE payer_id = :user_name")
    List<Payment> queryPaymentByPayerId(String user_name);

    // 根据患者用户名、支付日期和科室id查询支付记录
    @Query("SELECT * FROM payments WHERE payer_id = :user_name " +
            "AND pay_time = :pay_time AND appointment_id IN " +
            "(SELECT appointment_id FROM appointments WHERE doctor_id IN " +
            "(SELECT doctor_id FROM doctor_infos WHERE (office_id = :office_id OR :office_id = -1)))")
    List<Payment> queryPaymentByPayerIdAndPayTimeAndOfficeId(String user_name, Date pay_time, int office_id);

    // 根据患者用户名和科室id查询支付记录
    @Query("SELECT * FROM payments WHERE payer_id = :user_name " +
            "AND appointment_id IN " +
            "(SELECT appointment_id FROM appointments WHERE doctor_id IN " +
            "(SELECT doctor_id FROM doctor_infos WHERE (office_id = :office_id OR :office_id = -1)))")
    List<Payment> queryPaymentByPayerIdAndOfficeId(String user_name, int office_id);

}
