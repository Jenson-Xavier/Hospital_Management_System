package com.example.hospitalsystem.db_Entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.util.Date;

@Entity(tableName = "appointments",
        foreignKeys = {
                @ForeignKey(entity = Patient.class,
                        parentColumns = "userId",
                        childColumns = "patient_id",
                        onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = Doctor.class,
                        parentColumns = "userId",
                        childColumns = "doctor_id",
                        onDelete = ForeignKey.CASCADE),
                @ForeignKey(entity = Schedule.class,
                        parentColumns = "schedule_id",
                        childColumns = "schedule_id",
                        onDelete = ForeignKey.CASCADE)
        },
        indices = {@Index("patient_id"), @Index("doctor_id"), @Index("schedule_id")})
public class Appointment {

    public static final int FORENOON = 1;
    public static final int AFTERNOON = 2;

    public static final String[] periodStr = {
            "", "8:30-12:00", "13:30~17:00"
    };

    public static final int WAIT = 1;
    public static final int DONE = 2;
    public static final int CANCELLED = 3;

    public static final String[] stateStr = {
            "", "待看诊", "已看诊", "已取消"
    };

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "appointment_id")
    private int appointmentId;

    @ColumnInfo(name = "patient_id")
    private String patientId;

    @ColumnInfo(name = "doctor_id")
    private String doctorId;

    @ColumnInfo(name = "schedule_id")
    private int scheduleId;

    @ColumnInfo(name = "visit_date")
    @NonNull
    private Date visitDate;

    @ColumnInfo(name = "visit_period")
    private int visitPeriod; // Consider using a different type if needed

    @ColumnInfo(name = "serial_num")
    private int serialNum;

    @ColumnInfo(name = "order_state")
    private int orderState;

    @ColumnInfo(name = "order_time")
    @NonNull
    private Date orderTime;

    public Appointment() {
        orderTime = null;
        visitDate = null;
    }

    // Getters and Setters
    public int getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(int appointmentId) {
        this.appointmentId = appointmentId;
    }

    @NonNull
    public String getPatientId() {
        return patientId;
    }

    public void setPatientId(@NonNull String patientId) {
        this.patientId = patientId;
    }

    @NonNull
    public String getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(@NonNull String doctorId) {
        this.doctorId = doctorId;
    }

    public int getScheduleId() {
        return scheduleId;
    }

    public void setScheduleId(int scheduleId) {
        this.scheduleId = scheduleId;
    }

    public Date getVisitDate() {
        return visitDate;
    }

    public void setVisitDate(Date visitDate) {
        this.visitDate = visitDate;
    }

    public int getVisitPeriod() {
        return visitPeriod;
    }

    public void setVisitPeriod(int visitPeriod) {
        this.visitPeriod = visitPeriod;
    }

    public int getSerialNum() {
        return serialNum;
    }

    public void setSerialNum(int serialNum) {
        this.serialNum = serialNum;
    }

    public int getOrderState() {
        return orderState;
    }

    public void setOrderState(int orderState) {
        this.orderState = orderState;
    }

    public Date getOrderTime() {
        return orderTime;
    }

    public void setOrderTime(Date orderTime) {
        this.orderTime = orderTime;
    }

    @Override
    public String toString() {
        return "Appointment{" +
                "appointmentId=" + appointmentId +
                ", patientId='" + patientId + '\'' +
                ", doctorId='" + doctorId + '\'' +
                ", scheduleId=" + scheduleId +
                ", visitDate=" + visitDate +
                ", visitPeriod=" + visitPeriod +
                ", serialNum=" + serialNum +
                ", orderState=" + orderState +
                ", orderTime=" + orderTime +
                '}';
    }
}
