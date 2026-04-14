package com.example.hospitalsystem.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.CreatedDate;

import javax.print.Doc;
import java.util.Date;

@Getter
@Setter
@ToString
@Entity
public class Appointment {

    // 预约时间段 分为上午段和下午段
    public static final int FORENOON = 1;
    public static final int AFTERNOON = 2;

    public static final String[] periodStr = {
            "不限", "8:30-12:00", "13:30~17:00"
    };

    // 预约状态 "待看诊", "已看诊", "已取消"
    public static final int WAIT = 1;
    public static final int DONE = 2;
    public static final int CANCELLED = 3;

    public static final String[] stateStr = {
            "不限", "待看诊", "已看诊", "已取消"
    };

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "appointment_id")
    private int appointment_id;

    // 外键 - 患者用户
    @ManyToOne
    @JoinColumn(name = "patient_id", referencedColumnName = "user_id", nullable = false)
    private Patient patient;

    // 外键 - 医生用户
    @ManyToOne
    @JoinColumn(name = "doctor_id", referencedColumnName = "user_id", nullable = false)
    private Doctor doctor;

    // 外键 - 排班记录
    @ManyToOne
    @JoinColumn(name = "schedule_id", nullable = false)
    private Schedule schedule;

    // 就诊日期
    @Column(name = "visit_date", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date visit_date;

    // 就诊时段
    @Column(name = "visit_period", nullable = false)
    private int visit_period;

    // 预约序号
    @Column(name = "serial_num", nullable = false)
    private int serial_num;

    // 预约状态
    @Column(name = "order_state", nullable = false)
    private int order_state;

    // 预约时间
    @CreatedDate
    @Column(name = "order_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date order_time;

    @JsonIgnore
    @OneToOne(mappedBy = "appointment", cascade = CascadeType.ALL)
    private Payment payment;

}
