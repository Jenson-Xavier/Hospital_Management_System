package com.example.hospitalsystem.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.apache.ibatis.annotations.One;

import java.util.List;

@Getter
@Setter
@ToString
@Entity
public class Office {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "office_id", nullable = false)
    private int office_id;

    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @Column(name = "intro", nullable = false, columnDefinition = "LONGTEXT")
    @Lob
    private String intro;

    // 科室名称
    public static final String[] mNameArray = {
            "内科", "外科", "妇产科", "中医科", "康复医学科", "儿童保健科", "儿科", "感染科", "口腔科", "耳鼻喉科",
            "眼科", "皮肤科", "结核病科", "药剂科", "功能科", "影像科", "营养科"
    };

    @JsonIgnore
    @OneToMany(mappedBy = "office", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<DoctorInfo> doctorInfoList;

    @JsonIgnore
    @OneToMany(mappedBy = "office", cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<Schedule> scheduleList;

}
