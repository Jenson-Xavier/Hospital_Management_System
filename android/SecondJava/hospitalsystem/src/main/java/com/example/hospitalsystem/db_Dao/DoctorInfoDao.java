package com.example.hospitalsystem.db_Dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;


import com.example.hospitalsystem.db_Entity.DoctorInfo;
import com.example.hospitalsystem.db_Entity.Schedule;
import com.example.hospitalsystem.util.JDBCUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class DoctorInfoDao {

    // 根据医生id 查询医生信息
    // input: String doctor_id
    // return: DoctorInfo
    public DoctorInfo searchByDoctorId(String doctor_id) {
        DoctorInfo doctorInfo = null;
        Connection connection = JDBCUtil.getConn();
        // 构建 SQL 查询语句
        StringBuilder sql = new StringBuilder("select * from doctor_infos where doctor_id = ?");

        try {
            if (connection != null) {
                PreparedStatement ps = connection.prepareStatement(sql.toString());
                // 设置参数
                ps.setString(1, doctor_id); // 模糊匹配 doctor_name
                ResultSet rs = ps.executeQuery();
                // 遍历结果集
                if (rs.next()) {
                    int doctor_info_id = rs.getInt("doctor_info_id");
                    int office_id = rs.getInt("office_id");
                    int gender = rs.getInt("gender");
                    int seniority = rs.getInt("seniority");
                    String name = rs.getString("name");
                    String intro = rs.getString("intro");

                    doctorInfo = new DoctorInfo();
                    doctorInfo.setDoctorInfoId(doctor_info_id);
                    doctorInfo.setDoctorId(doctor_id);
                    doctorInfo.setOfficeId(office_id);
                    doctorInfo.setGender(gender);
                    doctorInfo.setSeniority(seniority);
                    doctorInfo.setName(name);
                    doctorInfo.setIntro(intro);
                }

                // 关闭资源
                rs.close();
                ps.close();
                return doctorInfo;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return doctorInfo; // 返回查询结果
    }

    // 获取所有医生信息
    // input: 无
    // return: List<DoctorInfo>
    public List<DoctorInfo> searchAll() {
        List<DoctorInfo> doctorInfoList = new ArrayList<>();
        Connection connection = JDBCUtil.getConn();
        // 构建 SQL 查询语句
        StringBuilder sql = new StringBuilder("select * from doctor_infos");
        try {
            if (connection != null) {
                PreparedStatement ps = connection.prepareStatement(sql.toString());
                ResultSet rs = ps.executeQuery();
                // 遍历结果集
                while (rs.next()) {
                    int doctor_info_id = rs.getInt("doctor_info_id");
                    int office_id = rs.getInt("office_id");
                    int gender = rs.getInt("gender");
                    int seniority = rs.getInt("seniority");
                    String doctor_id = rs.getString("doctor_id");
                    String name = rs.getString("name");
                    String intro = rs.getString("intro");

                    DoctorInfo doctorInfo = new DoctorInfo();
                    doctorInfo.setDoctorInfoId(doctor_info_id);
                    doctorInfo.setDoctorId(doctor_id);
                    doctorInfo.setOfficeId(office_id);
                    doctorInfo.setGender(gender);
                    doctorInfo.setSeniority(seniority);
                    doctorInfo.setName(name);
                    doctorInfo.setIntro(intro);

                    doctorInfoList.add(doctorInfo);
                }

                // 关闭资源
                rs.close();
                ps.close();
                return doctorInfoList;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return doctorInfoList; // 返回查询结果
    }

    // 根据科室id和医生姓名模糊查询医生信息
    // input: int office_id, String doctor_name
    // return: List<DoctorInfo>
    public List<DoctorInfo> searchByOfficeIdAndDoctorName(int office_id, String doctor_name) {
        List<DoctorInfo> doctorInfoList = new ArrayList<>();
        Connection connection = JDBCUtil.getConn();
        // 构建 SQL 查询语句
        StringBuilder sql = new StringBuilder("select * from doctor_infos where name like ? " +
                "and (? = -1 or office_id = ?)");
        try {
            if (connection != null) {
                PreparedStatement ps = connection.prepareStatement(sql.toString());
                ps.setString(1, "%" + doctor_name + "%");
                ps.setInt(2, office_id);
                ps.setInt(3, office_id);
                ResultSet rs = ps.executeQuery();
                // 遍历结果集
                while (rs.next()) {
                    int doctor_info_id = rs.getInt("doctor_info_id");
                    int gender = rs.getInt("gender");
                    int seniority = rs.getInt("seniority");
                    int officeId = rs.getInt("office_id");
                    String doctor_id = rs.getString("doctor_id");
                    String name = rs.getString("name");
                    String intro = rs.getString("intro");

                    DoctorInfo doctorInfo = new DoctorInfo();
                    doctorInfo.setDoctorInfoId(doctor_info_id);
                    doctorInfo.setDoctorId(doctor_id);
                    doctorInfo.setOfficeId(officeId);
                    doctorInfo.setGender(gender);
                    doctorInfo.setSeniority(seniority);
                    doctorInfo.setName(name);
                    doctorInfo.setIntro(intro);

                    doctorInfoList.add(doctorInfo);
                }

                // 关闭资源
                rs.close();
                ps.close();
                return doctorInfoList;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return doctorInfoList; // 返回查询结果
    }

}
