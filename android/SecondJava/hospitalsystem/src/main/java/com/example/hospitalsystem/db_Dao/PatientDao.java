package com.example.hospitalsystem.db_Dao;

import android.util.Log;

import com.example.hospitalsystem.db_Entity.Patient;
import com.example.hospitalsystem.util.JDBCUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class PatientDao {

    private static final String TAG = "mysql-hospitalsystem-JDBCUtil";

    // 登录功能，根据患者用户名查询患者
    // input: username
    // return：Patient
    public Patient searchById(String username) {
        Patient patient = null;
        Connection connection = JDBCUtil.getConn();
        int msg = 0;
        try {
            String sql = "select * from User where user_id = ?";
            if (connection != null) {
                PreparedStatement ps = connection.prepareStatement(sql);
                if (ps != null) {
                    ps.setString(1, username);
                    ResultSet rs = ps.executeQuery();
                    if (rs.next()) { // 检查是否有结果
                        patient = new Patient();
                        patient.setUserId(rs.getString("user_id"));
                        patient.setPassword(rs.getString("password"));
                        patient.setUserType(rs.getInt("user_type"));
                    }
                    rs.close();
                    ps.close();
                }
            }
        } catch (Exception e) {
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
        return patient;
    }

    // 注册功能，插入用户信息
    // input: Patient
    // return: 是否成功插入 成功(1) 失败(0, -1)
    public long insert(Patient patient) {
        Connection connection = JDBCUtil.getConn();
        long rowsAffected = -1;
        try {
            if (connection != null) {
                // 开启事务
                connection.setAutoCommit(false);
                // 插入到user表
                String sql = "insert into User (user_id, password, user_type) values (?, ?, ?)";
                PreparedStatement psUser = connection.prepareStatement(sql);
                psUser.setString(1, patient.getUserId());
                psUser.setString(2, patient.getPassword());
                psUser.setInt(3, patient.getUserType());
                psUser.executeUpdate();
                psUser.close();

                // 插入到 Patient 表
                String insertPatientSql = "insert into Patient (user_id) values (?)";
                PreparedStatement psPatient = connection.prepareStatement(insertPatientSql);
                psPatient.setString(1, patient.getUserId());
                rowsAffected = psPatient.executeUpdate();
                psPatient.close();

                // 提交事务
                connection.commit();
                return rowsAffected;
            }
        } catch (Exception e) {
            e.printStackTrace();
            try {
                if (connection != null) {
                    // 回滚事务
                    connection.rollback();
                }
            } catch (SQLException rollbackEx) {
                rollbackEx.printStackTrace();
            }
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return rowsAffected;
    }

    // 找回密码功能，修改用户信息
    // input: Patient
    // return: 是否成功修改
    public long update(Patient patient) {
        Connection connection = JDBCUtil.getConn();
        long rowsUpdated = -1;
        try {
            if (connection != null) {
                // 插入到user表
                String sql = "UPDATE User SET password = ? WHERE user_id = ?";
                PreparedStatement ps = connection.prepareStatement(sql);
                if (ps != null) {
                    // 从 patient 对象中获取修改后的值
                    ps.setString(1, patient.getPassword()); // 设置新的密码
                    ps.setString(2, patient.getUserId());   // 根据 user_id 定位记录

                    // 执行更新操作
                    rowsUpdated = ps.executeUpdate();

                    ps.close();
                }
                return rowsUpdated;
            }
        } catch (Exception e) {
            e.printStackTrace();
            try {
                if (connection != null) {
                    // 回滚事务
                    connection.rollback();
                }
            } catch (SQLException rollbackEx) {
                rollbackEx.printStackTrace();
            }
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return rowsUpdated;
    }

    // 注销功能
    // input: String username
    // return: 是否成功删除
    public long delete(String username) {
        Connection connection = JDBCUtil.getConn();
        long rowsAffected  = -1;
        try {
            if (connection != null) {
                connection.setAutoCommit(false);

                // 从Patient表中删除记录
                String sql = "DELETE FROM Patient WHERE user_id = ?";
                PreparedStatement psPatient = connection.prepareStatement(sql);
                if (psPatient != null) {
                    psPatient.setString(1, username);
                    // 执行删除操作
                    rowsAffected  = psPatient.executeUpdate();
                    psPatient.close();
                }
                // 从User表中删除记录
                String sql_user = "DELETE FROM User WHERE user_id = ?";
                PreparedStatement psUser = connection.prepareStatement(sql_user);
                if (psUser != null) {
                    psUser.setString(1, username);
                    // 执行删除操作
                    rowsAffected  = psUser.executeUpdate();
                    psUser.close();
                }
                connection.commit();
                return rowsAffected;
            }
        } catch (Exception e) {
            e.printStackTrace();
            try {
                if (connection != null) {
                    // 回滚事务
                    connection.rollback();
                }
            } catch (SQLException rollbackEx) {
                rollbackEx.printStackTrace();
            }
        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return rowsAffected;
    }
}
