package com.example.hospitalsystem.db_Dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.hospitalsystem.db_Entity.Office;
import com.example.hospitalsystem.db_Entity.Schedule;
import com.example.hospitalsystem.util.JDBCUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ScheduleDao {

    // 根据科室名称 模糊查询排班信息
    // input: int office_id
    // return: List<Schedule>
    public List<Schedule> searchByOfficeId(int office_id) {
        List<Schedule> scheduleList = new ArrayList<>();
        Connection connection = JDBCUtil.getConn();
        String sql = "select * from Schedule where office_id = ?";
        try {
            if (connection != null) {
                // 插入到user表
                PreparedStatement ps = connection.prepareStatement(sql);
                // 模糊查询
                ps.setInt(1, office_id);
                ResultSet rs = ps.executeQuery();

                // 遍历结果集
                while (rs.next()) {
                    int schedule_id = rs.getInt("schedule_id");
                    String doctor_id = rs.getString("doctor_id");
                    int schedule_period = rs.getInt("schedule_period");
                    int appointment_num = rs.getInt("appointment_num");
                    Date schedule_date = rs.getDate("schedule_date");

                    // 创建 schedule 对象并添加到列表
                    Schedule schedule = new Schedule();
                    schedule.setScheduleId(schedule_id);
                    schedule.setOfficeId(office_id);
                    schedule.setDoctorId(doctor_id);
                    schedule.setScheduleDate(schedule_date);
                    schedule.setSchedulePeriod(schedule_period);
                    schedule.setAppointmentNum(appointment_num);

                    scheduleList.add(schedule);
                }

                // 关闭资源
                rs.close();
                ps.close();
                return scheduleList;
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
        return scheduleList;
    }

    // 根据科室、日期、医生姓名三重条件 模糊查询排班信息
    // input: int officeId, String date, String doctorName
    // return: List<Schedule>
    public List<Schedule> searchByOfficeIdAndDoctorName(int officeId, String date, String doctorName) {
        List<Schedule> scheduleList = new ArrayList<>();
        Connection connection = JDBCUtil.getConn();

        // 构建 SQL 查询语句
        StringBuilder sql = new StringBuilder(
                "SELECT * FROM Schedule WHERE office_id = ? AND doctor_id IN " +
                        "(SELECT doctor_id FROM doctor_infos WHERE name LIKE ?)"
        );

        // 如果 date 不为空，则添加 schedule_date 条件
        if (date != null && !date.isEmpty()) {
            sql.append(" AND schedule_date = ?");
        }

        try {
            if (connection != null) {
                PreparedStatement ps = connection.prepareStatement(sql.toString());

                // 设置参数
                ps.setLong(1, officeId); // 设置 office_id
                ps.setString(2, "%" + doctorName + "%"); // 模糊匹配 doctor_name

                // 如果 date 不为空，设置 schedule_date 参数
                if (date != null && !date.isEmpty()) {
                    ps.setDate(3, java.sql.Date.valueOf(date));
                }

                ResultSet rs = ps.executeQuery();

                // 遍历结果集
                while (rs.next()) {
                    int schedule_id = rs.getInt("schedule_id");
                    String doctor_id = rs.getString("doctor_id");
                    int schedule_period = rs.getInt("schedule_period");
                    int appointment_num = rs.getInt("appointment_num");
                    Date schedule_date = rs.getDate("schedule_date");

                    // 创建 schedule 对象并添加到列表
                    Schedule schedule = new Schedule();
                    schedule.setScheduleId(schedule_id);
                    schedule.setOfficeId(officeId);
                    schedule.setDoctorId(doctor_id);
                    schedule.setScheduleDate(schedule_date);
                    schedule.setSchedulePeriod(schedule_period);
                    schedule.setAppointmentNum(appointment_num);

                    scheduleList.add(schedule);
                }

                // 关闭资源
                rs.close();
                ps.close();
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
        return scheduleList; // 返回查询结果
    }

    // 根据医生id查询排班信息
    // input: String doctor_id
    // return: List<Schedule>
    public List<Schedule> searchByDoctorId(String doctor_id) {
        List<Schedule> scheduleList = new ArrayList<>();
        Connection connection = JDBCUtil.getConn();
        String sql = "select * from Schedule where doctor_id = ?";
        try {
            if (connection != null) {
                // 插入到user表
                PreparedStatement ps = connection.prepareStatement(sql);
                ps.setString(1, doctor_id);
                ResultSet rs = ps.executeQuery();

                // 遍历结果集
                while (rs.next()) {
                    int schedule_id = rs.getInt("schedule_id");
                    int office_id = rs.getInt("office_id");
                    int schedule_period = rs.getInt("schedule_period");
                    int appointment_num = rs.getInt("appointment_num");
                    Date schedule_date = rs.getDate("schedule_date");

                    // 创建 schedule 对象并添加到列表
                    Schedule schedule = new Schedule();
                    schedule.setScheduleId(schedule_id);
                    schedule.setOfficeId(office_id);
                    schedule.setDoctorId(doctor_id);
                    schedule.setScheduleDate(schedule_date);
                    schedule.setSchedulePeriod(schedule_period);
                    schedule.setAppointmentNum(appointment_num);

                    scheduleList.add(schedule);
                }

                // 关闭资源
                rs.close();
                ps.close();
                return scheduleList;
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
        return scheduleList;
    }


    // 根据医生id、日期、时段查询排班信息
    // input: String doctor_id
    // return: List<Schedule>
    public List<Schedule> searchByDoctorIdAndDateAndPeriod(String doctor_id, String date, int period) {
        List<Schedule> scheduleList = new ArrayList<>();
        Connection connection = JDBCUtil.getConn();
        StringBuilder sql = new StringBuilder("select * from Schedule where doctor_id = ? and " +
                "(schedule_period = ? or ? = 0)");

        // 如果 date 不为空，则添加 schedule_date 条件
        if (date != null && !date.isEmpty()) {
            sql.append(" and schedule_date = ?");
        }

        try {
            if (connection != null) {
                // 插入到user表
                PreparedStatement ps = connection.prepareStatement(sql.toString());
                ps.setString(1, doctor_id);
                ps.setInt(2, period);
                ps.setInt(3, period);

                // 如果 date 不为空，设置 schedule_date 参数
                if (date != null && !date.isEmpty()) {
                    ps.setDate(4, java.sql.Date.valueOf(date));
                }

                ResultSet rs = ps.executeQuery();

                // 遍历结果集
                while (rs.next()) {
                    int schedule_id = rs.getInt("schedule_id");
                    int office_id = rs.getInt("office_id");
                    int schedule_period = rs.getInt("schedule_period");
                    int appointment_num = rs.getInt("appointment_num");
                    Date schedule_date = rs.getDate("schedule_date");

                    // 创建 schedule 对象并添加到列表
                    Schedule schedule = new Schedule();
                    schedule.setScheduleId(schedule_id);
                    schedule.setOfficeId(office_id);
                    schedule.setDoctorId(doctor_id);
                    schedule.setScheduleDate(schedule_date);
                    schedule.setSchedulePeriod(schedule_period);
                    schedule.setAppointmentNum(appointment_num);

                    scheduleList.add(schedule);
                }

                // 关闭资源
                rs.close();
                ps.close();
                return scheduleList;
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
        return scheduleList;
    }

}
