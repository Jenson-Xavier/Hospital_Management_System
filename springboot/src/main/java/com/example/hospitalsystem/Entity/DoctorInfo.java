package com.example.hospitalsystem.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Entity(name = "doctor_infos")
public class DoctorInfo {

    // 性别 MALE 男 FEMALE 女
    public static final int MALE = 1;
    public static final int FEMALE = 2;

    public static final String[] ganderStr = {
            "不限", "男", "女"
    };

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "doctor_info_id", nullable = false)
    private int doctor_info_id;

    // 外键 - 医生用户
    @OneToOne
    @JoinColumn(name = "doctor_id", nullable = false, referencedColumnName = "user_id")
    private Doctor doctor;
//    private String doctor_id;

    // 外键 - 科室
    @ManyToOne
    @JoinColumn(name = "office_id", referencedColumnName = "office_id", nullable = false)
    private Office office;

    // 外键 - 医生姓名
    @Column(name = "name", nullable = false)
    private String name;

    // 性别
    @Column(name = "gender", nullable = false)
    private int gender;

    // 工龄
    @Column(name = "seniority", nullable = false)
    private int seniority;

    // 介绍
    @Column(name = "intro", columnDefinition = "LONGTEXT")
    @Lob
    private String intro;


}
