package com.example.hospitalsystem.Entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.UUID;

@Getter
@Setter
@ToString
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public class User {

    public static final int PATIENT =1;
    public static final int DOCTOR =2;
    public static final int ADMIN =3;

    @Id
    @Column(name = "user_id",nullable = false)
    private String user_id;

//    @JsonIgnore     // 让springmvc把当前对象转换成json字符串时 忽略password
    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "user_type", nullable = false)
    private int user_type;


}
