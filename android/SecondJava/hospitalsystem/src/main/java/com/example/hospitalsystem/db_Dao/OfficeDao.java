package com.example.hospitalsystem.db_Dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.hospitalsystem.db_Entity.Office;
import com.example.hospitalsystem.db_Entity.Patient;
import com.example.hospitalsystem.util.JDBCUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class OfficeDao {

    // 获取全部科室信息
    // input: 无
    // return: List<Office>
    public List<Office> searchAll() {
        List<Office> officeList = new ArrayList<>();
        Connection connection = JDBCUtil.getConn();
        String sql = "select * from Office";
        try {
            if (connection != null) {
                // 插入到user表
                PreparedStatement ps = connection.prepareStatement(sql);
                ResultSet rs = ps.executeQuery();

                // 遍历结果集
                while (rs.next()) {
                    int officeId = rs.getInt("office_id");
                    String name = rs.getString("name");
                    String intro = rs.getString("intro");

                    // 创建 Office 对象并添加到列表
                    Office office = new Office();
                    office.setOfficeId(officeId);
                    office.setName(name);
                    office.setIntro(intro);

                    officeList.add(office);
                }

                // 关闭资源
                rs.close();
                ps.close();
                return officeList;
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
        return officeList;
    }

    // 根据科室名称部分匹配 查询科室信息
    // input: String partialName
    // return: List<Office>
    public List<Office> searchByName(String partialName) {
        List<Office> officeList = new ArrayList<>();
        Connection connection = JDBCUtil.getConn();
        String sql = "select * from Office where name like ?";
        try {
            if (connection != null) {
                // 插入到user表
                PreparedStatement ps = connection.prepareStatement(sql);
                // 模糊查询
                ps.setString(1, "%" + partialName + "%");
                ResultSet rs = ps.executeQuery();

                // 遍历结果集
                while (rs.next()) {
                    int officeId = rs.getInt("office_id");
                    String name = rs.getString("name");
                    String intro = rs.getString("intro");

                    // 创建 Office 对象并添加到列表
                    Office office = new Office();
                    office.setOfficeId(officeId);
                    office.setName(name);
                    office.setIntro(intro);

                    officeList.add(office);
                }

                // 关闭资源
                rs.close();
                ps.close();
                return officeList;
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
        return officeList;
    }

    // 根据科室id查询科室
    // input: int office_id
    // return: Office
    public Office searchById(int office_id) {
        Office office = null;
        Connection connection = JDBCUtil.getConn();
        String sql = "select * from Office where office_id = ?";
        try {
            if (connection != null) {
                PreparedStatement ps = connection.prepareStatement(sql);
                ps.setInt(1, office_id);
                ResultSet rs = ps.executeQuery();

                if (rs.next()) {
                    String name = rs.getString("name");
                    String intro = rs.getString("intro");

                    // 创建 Office 对象并添加到列表
                    office = new Office();
                    office.setOfficeId(office_id);
                    office.setName(name);
                    office.setIntro(intro);
                }

                // 关闭资源
                rs.close();
                ps.close();
                return office;
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
        return office;
    }

    // 根据科室id查询科室
    // input: String doctor_id
    // return: Office
    public Office searchByDoctorId(String doctor_id) {
        Office office = null;
        Connection connection = JDBCUtil.getConn();
        String sql = "select * from Office where office_id in " +
                "(select office_id from doctor_infos where doctor_id = ?)";
        try {
            if (connection != null) {
                PreparedStatement ps = connection.prepareStatement(sql);
                ps.setString(1, doctor_id);
                ResultSet rs = ps.executeQuery();

                if (rs.next()) {
                    String name = rs.getString("name");
                    String intro = rs.getString("intro");
                    int office_id = rs.getInt("office_id");

                    // 创建 Office 对象并添加到列表
                    office = new Office();
                    office.setOfficeId(office_id);
                    office.setName(name);
                    office.setIntro(intro);
                }

                // 关闭资源
                rs.close();
                ps.close();
                return office;
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
        return office;
    }

    // 根据支付记录id查询科室
    // input: int payment_id
    // return: Office
    public Office searchByPaymentId(int payment_id) {
        Office office = null;
        Connection connection = JDBCUtil.getConn();
        String sql = "select * from Office where office_id in " +
                "(select office_id from doctor_infos where doctor_id in " +
                "(select doctor_id from Appointment where appointment_id in " +
                "(select appointment_id from Payment where payment_id = ?)))";
        try {
            if (connection != null) {
                PreparedStatement ps = connection.prepareStatement(sql);
                ps.setInt(1, payment_id);
                ResultSet rs = ps.executeQuery();

                if (rs.next()) {
                    String name = rs.getString("name");
                    String intro = rs.getString("intro");
                    int office_id = rs.getInt("office_id");

                    // 创建 Office 对象并添加到列表
                    office = new Office();
                    office.setOfficeId(office_id);
                    office.setName(name);
                    office.setIntro(intro);
                }

                // 关闭资源
                rs.close();
                ps.close();
                return office;
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
        return office;
    }

}
