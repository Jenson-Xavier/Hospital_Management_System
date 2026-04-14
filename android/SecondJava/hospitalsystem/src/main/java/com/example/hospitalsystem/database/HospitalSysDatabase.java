package com.example.hospitalsystem.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.example.hospitalsystem.Converters.Converters;
import com.example.hospitalsystem.dao.AppointmentDao;
import com.example.hospitalsystem.dao.CommentDao;
import com.example.hospitalsystem.dao.DoctorDao;
import com.example.hospitalsystem.dao.DoctorInfoDao;
import com.example.hospitalsystem.dao.OfficeDao;
import com.example.hospitalsystem.dao.PatientDao;
import com.example.hospitalsystem.dao.PaymentDao;
import com.example.hospitalsystem.dao.ScheduleDao;
import com.example.hospitalsystem.entity.Appointment;
import com.example.hospitalsystem.entity.Comment;
import com.example.hospitalsystem.entity.Doctor;
import com.example.hospitalsystem.entity.DoctorInfo;
import com.example.hospitalsystem.entity.Office;
import com.example.hospitalsystem.entity.Patient;
import com.example.hospitalsystem.entity.Payment;
import com.example.hospitalsystem.entity.Refund;
import com.example.hospitalsystem.entity.Schedule;
import com.example.hospitalsystem.entity.User;

@Database(entities = {User.class, Patient.class, Doctor.class, Office.class, Schedule.class,
        Appointment.class, DoctorInfo.class, Payment.class, Refund.class, Comment.class}, version=1, exportSchema = true)
@TypeConverters({Converters.class})
public abstract class HospitalSysDatabase extends RoomDatabase {

    // 获取该数据库中某张表的持久化对象
    public abstract PatientDao patientDao();
    public abstract OfficeDao officeDao();
    public abstract DoctorDao doctorDao();
    public abstract DoctorInfoDao doctorInfoDao();
    public abstract ScheduleDao scheduleDao();
    public abstract AppointmentDao appointmentDao();
    public abstract PaymentDao paymentDao();
    public abstract CommentDao commentDao();
}
