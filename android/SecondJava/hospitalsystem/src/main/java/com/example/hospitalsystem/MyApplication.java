package com.example.hospitalsystem;

import android.app.Application;
import android.content.res.Configuration;
import android.os.Environment;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.room.Room;

import com.example.hospitalsystem.database.HospitalSysDatabase;
import com.example.hospitalsystem.entity.Comment;
import com.example.hospitalsystem.entity.Doctor;
import com.example.hospitalsystem.entity.DoctorInfo;
import com.example.hospitalsystem.entity.Office;
import com.example.hospitalsystem.entity.Schedule;
import com.example.hospitalsystem.util.SharedUtil;

import java.io.File;
import java.util.List;

public class MyApplication extends Application {

    private static MyApplication mApp;

    // 声明一个用户数据库对象
    private HospitalSysDatabase hospitalDB;

    public static MyApplication getInstance() {
        return mApp;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mApp = this;
        Log.d("tjy", "数据库未建立");
        // 构建数据库实例
        hospitalDB = Room.databaseBuilder(this, HospitalSysDatabase.class,  "hospital_db")
                // 允许迁移数据库
                .addMigrations()
                // 允许在主线程中操作数据库
                .allowMainThreadQueries()
                .build();
        Log.d("tjy", "数据库已经建立");
//        initOfficeInfo();
//        initDoctor();
//        initDoctorInfo();
//        clearSchedule();
//        initSchedule();
//        initComment();
//        initAppointment();
    }

    // 在APP终止时调用
    @Override
    public void onTerminate() {
        super.onTerminate();
    }

    // 在配置改变时调用，如横屏变为竖屏
    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    // 获取数据库的实例
    public HospitalSysDatabase getHospitalDB(){
        return hospitalDB;
    }

    // 初始化科室信息
    public void initOfficeInfo() {
        // 首次读取结果为true，之后读取均为false
        boolean isFirst = SharedUtil.getInstance(this).readBoolean("first", true);
        // 获取当前APP的私有下载路径
        String directory = getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).toString() + File.separatorChar;
        if (isFirst) {
            // 获取默认科室信息列表
            List<Office> officeList = Office.getDefaultList();
            hospitalDB.officeDao().insertOfficeInfos(officeList);
            // 首次会写入结果false
            SharedUtil.getInstance(this).writeBoolean("first", false);
        }
    }

    // 初始化医生
    public void initDoctor() {
        // 首次读取结果为true，之后读取均为false
        boolean isFirst = SharedUtil.getInstance(this).readBoolean("doctor", true);
        // 获取当前APP的私有下载路径
        String directory = getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).toString() + File.separatorChar;
        if (isFirst) {
            // 获取默认科室信息列表
            List<Doctor> doctorList = Doctor.getDefaultList();
            hospitalDB.doctorDao().insertDoctors(doctorList);
            // 首次会写入结果false
            SharedUtil.getInstance(this).writeBoolean("doctor", false);
        }
    }

    // 初始化医生信息
    public void initDoctorInfo() {
        // 首次读取结果为true，之后读取均为false
        boolean isFirst = SharedUtil.getInstance(this).readBoolean("doctorInfo", true);
        // 获取当前APP的私有下载路径
        String directory = getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).toString() + File.separatorChar;
        if (isFirst) {
            // 获取默认科室信息列表
            List<DoctorInfo> doctorInfoList = DoctorInfo.getDefaultList();
            hospitalDB.doctorInfoDao().insertDoctorInfos(doctorInfoList);
            // 首次会写入结果false
            SharedUtil.getInstance(this).writeBoolean("doctorInfo", false);
        }
    }

    // 初始化排班信息
    public void initSchedule() {
        // 首次读取结果为true，之后读取均为false
        boolean isFirst = SharedUtil.getInstance(this).readBoolean("schedule", true);
        // 获取当前APP的私有下载路径
        String directory = getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).toString() + File.separatorChar;
        if (isFirst) {
            // 获取默认科室信息列表
            List<Schedule> scheduleList = Schedule.getDefaultList();
            hospitalDB.scheduleDao().insertScheduleInfos(scheduleList);
            // 首次会写入结果false
            SharedUtil.getInstance(this).writeBoolean("schedule", false);
        }
    }

    // 清除排班信息
    public void clearSchedule() {
        SharedUtil.getInstance(this).writeBoolean("schedule", true);
        hospitalDB.scheduleDao().clear();
    }

    // 初始化预约记录
    public void initAppointment() {
        hospitalDB.appointmentDao().clear();
    }

    // 初始化评论信息
    public void initComment() {
        // 首次读取结果为true，之后读取均为false
        boolean isFirst = SharedUtil.getInstance(this).readBoolean("comment", true);
        // 获取当前APP的私有下载路径
        String directory = getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).toString() + File.separatorChar;
        if (isFirst) {
            // 获取默认科室信息列表
            List<Comment> commentList = Comment.getDefaultList();
            hospitalDB.commentDao().insertComments(commentList);
            // 首次会写入结果false
            SharedUtil.getInstance(this).writeBoolean("comment", false);
        }
    }

}