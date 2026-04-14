package com.example.hospitalsystem.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.CreatedDate;

import java.util.Date;

@Getter
@Setter
@ToString
@Entity
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int payment_id;

    // 外键 - 预约记录
    @OneToOne
    @JoinColumn(name = "appointment_id", referencedColumnName = "appointment_id", nullable = false)
    private Appointment appointment;

    // 外键 - 患者用户
    @ManyToOne
    @JoinColumn(name = "patient_id", referencedColumnName = "user_id", nullable = false)
    private Patient payer;

    // 支付金额
    @Column(name = "payment_amount", nullable = false)
    private float payment_amount;

    // 支付时间
    @CreatedDate
    @Column(name = "pay_time", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date pay_time;

    @OneToOne(mappedBy = "payment", cascade = CascadeType.ALL)
    private Refund refund;

}
