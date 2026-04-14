package com.example.hospitalsystem.db_Entity;

import androidx.room.Entity;

import java.util.ArrayList;

@Entity
public class Doctor extends User {

    // 预先插入若干数据
    // 医生工号列表
    private static final String[] mDoctorIdArray = {
            "00000000001", "00000000002", "00000000003", "00000000004", "00000000005",
            "00000000006", "00000000007", "00000000008", "00000000009", "00000000010", "00000000011",
            "00000000012", "00000000013", "00000000014", "00000000015", "00000000016", "00000000017"
    };

    public static ArrayList<Doctor> getDefaultList() {
        ArrayList<Doctor> doctorList = new ArrayList<Doctor>();
        for (int i = 1; i <= mDoctorIdArray.length; ++i) {
            Doctor info = new Doctor();
            info.setUserId(mDoctorIdArray[i-1]);
            info.setPassword("123456"); // 初始密码均设置为123456
            info.setUserType(User.DOCTOR);
            doctorList.add(info);
        }
        return doctorList;
    }

}
