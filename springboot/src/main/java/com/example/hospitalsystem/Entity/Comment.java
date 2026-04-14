package com.example.hospitalsystem.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.CreatedDate;

import java.util.Date;

@Getter
@Setter
@ToString
@Entity
public class Comment {

    public static final int WAIT = 1;
    public static final int PASSED  = 2;
    public static final int UNPASSED = 3;

    public static final String[] stateStr = {
            "不限", "未审核", "通过", "未通过"
    };

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    private int comment_id;

    // 外键 - 患者用户
    @ManyToOne
    @JoinColumn(name = "patient_id", referencedColumnName = "user_id", nullable = false)
    private Patient publisher;

    // 外键 - 医生用户
    @ManyToOne
    @JoinColumn(name = "doctor_id", referencedColumnName = "user_id", nullable = false)
    private Doctor commented;

    // 发布时间
    @CreatedDate
    @Column(name = "publish_time", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date publish_time;

    // 评论内容
    @Column(name = "comment_text", nullable = false)
    @Lob
    private String comment_text;

    // 评论状态
    @Column(name = "audit", nullable = false)
    private int audit;

}
