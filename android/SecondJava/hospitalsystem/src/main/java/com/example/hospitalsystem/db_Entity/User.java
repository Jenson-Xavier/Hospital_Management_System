package com.example.hospitalsystem.db_Entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class User {

    public static final int PATIENT =1;
    public static final int DOCTOR =2;
    public static final int ADMIN =3;

    @PrimaryKey
    @NonNull
    private String userId;

    @ColumnInfo(name = "password")
    private String password;

    @ColumnInfo(name = "user_type")
    private int userType;

    public User() {
        userId = "12312312312";
    }

    // Getters and Setters
    @NonNull
    public String getUserId() {
        return userId;
    }

    public void setUserId(@NonNull String userId) {
        this.userId = userId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getUserType() {
        return userType;
    }

    public void setUserType(int userType) {
        this.userType = userType;
    }

    @Override
    public String toString() {
        return "User{" +
                "userId='" + userId + '\'' +
                ", password='" + password + '\'' +
                ", userType=" + userType +
                '}';
    }
}
