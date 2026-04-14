package com.example.hospitalsystem.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import java.util.Date;

@Entity(tableName = "refunds",
        foreignKeys = @ForeignKey(entity = Payment.class,
                parentColumns = "payment_id",
                childColumns = "payment_id",
                onDelete = ForeignKey.CASCADE),
        indices = {@Index("payment_id")})
public class Refund {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "refund_id")
    private int refundId;

    @ColumnInfo(name = "payment_id")
    private int paymentId;

    @ColumnInfo(name = "refund_time")
    private Date refundTime;

    // Getters and Setters
    public int getRefundId() {
        return refundId;
    }

    public void setRefundId(int refundId) {
        this.refundId = refundId;
    }

    public int getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(int paymentId) {
        this.paymentId = paymentId;
    }

    public Date getRefundTime() {
        return refundTime;
    }

    public void setRefundTime(Date refundTime) {
        this.refundTime = refundTime;
    }

    @Override
    public String toString() {
        return "Refund{" +
                "refundId=" + refundId +
                ", paymentId=" + paymentId +
                ", refundTime=" + refundTime +
                '}';
    }
}
