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
public class Doctor extends User{

    @JsonIgnore
    @OneToMany(mappedBy = "doctor", cascade = CascadeType.ALL)
    private List<Appointment> appointmentList;

    @JsonIgnore
    @OneToOne(mappedBy = "doctor", cascade = CascadeType.ALL)
    private DoctorInfo doctorInfo;

    @JsonIgnore
    @OneToMany(mappedBy = "doctor", cascade = CascadeType.ALL)
    private List<Schedule> scheduleList;

    @JsonIgnore
    @OneToMany(mappedBy = "commented", cascade = CascadeType.ALL)
    private List<Comment> commentList;

}
