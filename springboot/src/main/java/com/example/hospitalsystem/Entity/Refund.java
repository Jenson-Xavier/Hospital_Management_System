package com.example.hospitalsystem.Entity;

import jakarta.persistence.*;
import org.springframework.data.annotation.CreatedDate;

import java.util.Date;

@Entity
public class Refund {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int refund_id;

    // 外键 - 支付记录
    @OneToOne
    @JoinColumn(name = "payment_id", referencedColumnName = "payment_id", nullable = false)
    private Payment payment;

    // 退款时间
    @CreatedDate
    @Column(name = "refund_time", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date refund_time;

}
