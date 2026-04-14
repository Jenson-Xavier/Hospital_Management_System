package com.example.hospitalsystem.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@ToString
@Entity
public class Schedule {

    // 排班时段
    public static final int FORENOON = 1;
    public static final int AFTERNOON = 2;

    public static final String[] periodStr = {
            "不限", "8:30-12:00", "13:30~17:00"
    };

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "schedule_id")
    private int schedule_id;

    // 外键 - 科室
    @ManyToOne
    @JoinColumn(name = "office_id", referencedColumnName = "office_id", nullable = false)
    private Office office;

    // 外键 - 医生
    @ManyToOne
    @JoinColumn(name = "doctor_id", nullable = false, referencedColumnName = "user_id")
    private Doctor doctor;

    // 排班日期
    @Column(name = "schedule_date", nullable = false)
    @Temporal(TemporalType.DATE)
    private Date schedule_date;

    // 排班时段
    @Column(name = "schedule_period", nullable = false)
    private int schedule_period;

    // 号量余额
    @Column(name = "appointment_num", nullable = false)
    private int appointment_num;

    @JsonIgnore
    @OneToMany(mappedBy = "schedule", cascade = CascadeType.ALL)
    private List<Appointment> appointmentList;

}
