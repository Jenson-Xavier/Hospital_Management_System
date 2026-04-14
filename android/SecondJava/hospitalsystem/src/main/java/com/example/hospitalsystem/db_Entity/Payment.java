package com.example.hospitalsystem.db_Entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.util.Date;

@Entity(tableName = "payments",
        foreignKeys = @ForeignKey(entity = Appointment.class,
                parentColumns = "appointment_id",
                childColumns = "appointment_id",
                onDelete = ForeignKey.CASCADE),
        indices = {@Index("appointment_id")})
public class Payment {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "payment_id")
    private int paymentId;

    @ColumnInfo(name = "appointment_id")
    private int appointmentId;

    @ColumnInfo(name = "payer_id")
    private String payerId;

    @ColumnInfo(name = "payment_amount")
    private float paymentAmount;

    @ColumnInfo(name = "pay_time")
    private Date payTime;

    // Getters and Setters
    public int getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(int paymentId) {
        this.paymentId = paymentId;
    }

    public int getAppointmentId() {
        return appointmentId;
    }

    public void setAppointmentId(int appointmentId) {
        this.appointmentId = appointmentId;
    }

    @NonNull
    public String getPayerId() {
        return payerId;
    }

    public void setPayerId(@NonNull String payerId) {
        this.payerId = payerId;
    }

    public float getPaymentAmount() {
        return paymentAmount;
    }

    public void setPaymentAmount(float paymentAmount) {
        this.paymentAmount = paymentAmount;
    }

    public Date getPayTime() {
        return payTime;
    }

    public void setPayTime(Date payTime) {
        this.payTime = payTime;
    }

    @Override
    public String toString() {
        return "Payment{" +
                "paymentId=" + paymentId +
                ", appointmentId=" + appointmentId +
                ", payerId='" + payerId + '\'' +
                ", paymentAmount=" + paymentAmount +
                ", payTime=" + payTime +
                '}';
    }
}
