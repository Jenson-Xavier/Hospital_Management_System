package com.example.hospitalsystem.db_Dao;

import com.example.hospitalsystem.db_Entity.Appointment;
import com.example.hospitalsystem.db_Entity.Patient;
import com.example.hospitalsystem.db_Entity.Schedule;
import com.example.hospitalsystem.util.JDBCUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AppointmentDao {

    private static final String TAG = "mysql-hospitalsystem-JDBCUtil";

    // 根据患者id删除预约记录
    // input: username
    // return：是否成功删除
    public long deleteByPatientId(String username) {
        Connection connection = JDBCUtil.getConn();
        long rowsAffected  = -1;
        try {
            if (connection != null) {
                // 插入到user表
                String sql = "DELETE FROM Appointment WHERE patient_id = ?";
                PreparedStatement ps = connection.prepareStatement(sql);
                if (ps != null) {
                    ps.setString(1, username);
                    // 执行删除操作
                    rowsAffected  = ps.executeUpdate();
                    ps.close();
                }
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

    // 查询某班排班信息的预约数量，即号量余额
    // input: int schedule_id
    // return：
    public long searchRemainByScheduleId(int schedule_id) {
        // List<Appointment> appointmentList = new ArrayList<>();
        long count = -1;
        Connection connection = JDBCUtil.getConn();
        try {
            if (connection != null) {
                // 插入到user表
                String sql = "select COUNT(*) from Appointment where schedule_id = ? and order_state != ?";
                PreparedStatement ps = connection.prepareStatement(sql);
                if (ps != null) {
                    ps.setInt(1, schedule_id);
                    ps.setInt(2, Appointment.CANCELLED);
                    ResultSet rs = ps.executeQuery();
                    while (rs.next()) {
                        count = rs.getInt(1);
                        /*int appointment_id = rs.getInt("appointment_id");
                        String patient_id = rs.getString("patient_id");
                        String doctor_id = rs.getString("doctor_id");
                        Date visit_date = rs.getDate("visit_date");
                        int visit_period = rs.getInt("visit_period");
                        int serial_num = rs.getInt("serial_num");
                        int order_state = rs.getInt("order_state");
                        Date order_time = rs.getDate("order_time");

                        Appointment appointment = new Appointment();
                        appointment.setAppointmentId(appointment_id);
                        appointment.setPatientId(patient_id);
                        appointment.setDoctorId(doctor_id);
                        appointment.setScheduleId(schedule_id);
                        appointment.setVisitDate(visit_date);
                        appointment.setVisitPeriod(visit_period);
                        appointment.setSerialNum(serial_num);
                        appointment.setOrderState(order_state);
                        appointment.setOrderTime(order_time);

                        appointmentList.add(appointment);*/
                    }
                    ps.close();
                }
                return count;
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
        return count;
    }

    // 查询患者用户是否已经预约过某一个排班信息
    // input: int schedule_id, String patient_id
    // return：Appointment
    public Appointment searchByScheduleIdAndPatientId(int schedule_id, String patient_id) {
        Appointment appointment = null;
        Connection connection = JDBCUtil.getConn();
        try {
            if (connection != null) {
                // 插入到user表
                String sql = "select * from Appointment where schedule_id = ? and patient_id = ? and order_state != ?";
                PreparedStatement ps = connection.prepareStatement(sql);
                if (ps != null) {
                    ps.setInt(1, schedule_id);
                    ps.setString(2, patient_id);
                    ps.setInt(3, Appointment.CANCELLED);
                    ResultSet rs = ps.executeQuery();
                    if (rs.next()) {
                        int appointment_id = rs.getInt("appointment_id");
                        String doctor_id = rs.getString("doctor_id");
                        Date visit_date = rs.getDate("visit_date");
                        int visit_period = rs.getInt("visit_period");
                        int serial_num = rs.getInt("serial_num");
                        int order_state = rs.getInt("order_state");
                        Date order_time = rs.getDate("order_time");

                        appointment = new Appointment();
                        appointment.setAppointmentId(appointment_id);
                        appointment.setPatientId(patient_id);
                        appointment.setDoctorId(doctor_id);
                        appointment.setScheduleId(schedule_id);
                        appointment.setVisitDate(visit_date);
                        appointment.setVisitPeriod(visit_period);
                        appointment.setSerialNum(serial_num);
                        appointment.setOrderState(order_state);
                        appointment.setOrderTime(order_time);
                    }
                    ps.close();
                }
                return appointment;
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
        return appointment;
    }

    // 新增预约记录
    // input: Appointment
    // return: 自增主键值 成功(>0) 失败(-1)
    public long insert(Appointment appointment) {
        Connection connection = JDBCUtil.getConn();
        long generatedId = -1;
        try {
            if (connection != null) {
                // 开启事务
                connection.setAutoCommit(false);
                // 插入到user表
                String sql = "insert into Appointment (patient_id, doctor_id, schedule_id, " +
                        "visit_date, visit_period, serial_num, order_state, order_time) values (?, ?, ?, ?, ?, ?, ?, ?)";
                PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, appointment.getPatientId());
                ps.setString(2, appointment.getDoctorId());
                ps.setInt(3, appointment.getScheduleId());
                ps.setDate(4, new java.sql.Date(appointment.getVisitDate().getTime()));
                ps.setInt(5, appointment.getVisitPeriod());
                ps.setInt(6, appointment.getSerialNum());
                ps.setInt(7, appointment.getOrderState());
                ps.setTimestamp(8, new java.sql.Timestamp(appointment.getOrderTime().getTime()));
                ps.executeUpdate();

                // 获取生成的主键
                ResultSet generatedKeys = ps.getGeneratedKeys();
                if (generatedKeys.next()) {
                    generatedId = generatedKeys.getLong(1); // 获取第一列的主键值
                }
                generatedKeys.close();
                ps.close();
                // 提交事务
                connection.commit();
                return generatedId;
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
        return generatedId;
    }

    // 根据患者用户名查询预约记录
    // input: String patient_id
    // return：List<Appointment>
    public List<Appointment> searchByPatientId(String patient_id) {
        List<Appointment> appointmentList = new ArrayList<>();
        Connection connection = JDBCUtil.getConn();
        try {
            if (connection != null) {
                // 插入到user表
                String sql = "select * from Appointment where patient_id = ?";
                PreparedStatement ps = connection.prepareStatement(sql);
                if (ps != null) {
                    ps.setString(1, patient_id);
                    ResultSet rs = ps.executeQuery();
                    while (rs.next()) {
                        int appointment_id = rs.getInt("appointment_id");
                        int schedule_id = rs.getInt("schedule_id");
                        String doctor_id = rs.getString("doctor_id");
                        Date visit_date = rs.getDate("visit_date");
                        int visit_period = rs.getInt("visit_period");
                        int serial_num = rs.getInt("serial_num");
                        int order_state = rs.getInt("order_state");
                        Date order_time = rs.getTimestamp("order_time");

                        Appointment appointment = new Appointment();
                        appointment.setAppointmentId(appointment_id);
                        appointment.setPatientId(patient_id);
                        appointment.setDoctorId(doctor_id);
                        appointment.setScheduleId(schedule_id);
                        appointment.setVisitDate(visit_date);
                        appointment.setVisitPeriod(visit_period);
                        appointment.setSerialNum(serial_num);
                        appointment.setOrderState(order_state);
                        appointment.setOrderTime(order_time);

                        appointmentList.add(appointment);
                    }
                    ps.close();
                }
                return appointmentList;
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
        return appointmentList;
    }

    // 根据医生姓名、患者用户名、日期和科室id查询排班信息
    // input: String doctor_id
    // return: List<Schedule>
    public List<Appointment> searchByDoctorNameAndPatientIdAndOfficeId(String doctor_name, String patient_id, int office_id, String date) {
        List<Appointment> appointmentList = new ArrayList<>();
        Connection connection = JDBCUtil.getConn();
        StringBuilder sql = new StringBuilder("select * from Appointment where patient_id = ? and doctor_id in " +
                " (select doctor_id from doctor_infos where (office_id = ? or ? = -1) and name like ?)");

        // 如果 date 不为空，则添加 schedule_date 条件
        if (date != null && !date.isEmpty()) {
            sql.append(" and visit_date = ?");
        }

        try {
            if (connection != null) {
                // 插入到user表
                PreparedStatement ps = connection.prepareStatement(sql.toString());
                ps.setString(1, patient_id);
                ps.setInt(2, office_id);
                ps.setInt(3, office_id);
                ps.setString(4, "%" + doctor_name + "%");

                // 如果 date 不为空，设置 schedule_date 参数
                if (date != null && !date.isEmpty()) {
                    ps.setDate(5, java.sql.Date.valueOf(date));
                }

                ResultSet rs = ps.executeQuery();
                // 遍历结果集
                while (rs.next()) {
                    int appointment_id = rs.getInt("appointment_id");
                    int schedule_id = rs.getInt("schedule_id");
                    String doctor_id = rs.getString("doctor_id");
                    Date visit_date = rs.getDate("visit_date");
                    int visit_period = rs.getInt("visit_period");
                    int serial_num = rs.getInt("serial_num");
                    int order_state = rs.getInt("order_state");
                    Date order_time = rs.getTimestamp("order_time");

                    Appointment appointment = new Appointment();
                    appointment.setAppointmentId(appointment_id);
                    appointment.setPatientId(patient_id);
                    appointment.setDoctorId(doctor_id);
                    appointment.setScheduleId(schedule_id);
                    appointment.setVisitDate(visit_date);
                    appointment.setVisitPeriod(visit_period);
                    appointment.setSerialNum(serial_num);
                    appointment.setOrderState(order_state);
                    appointment.setOrderTime(order_time);

                    appointmentList.add(appointment);
                }

                // 关闭资源
                rs.close();
                ps.close();
                return appointmentList;
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
        return appointmentList;
    }

    // 取消预约：修改预约记录状态
    // input: Appointment
    // return: 是否成功修改
    public long update(Appointment appointment) {
        Connection connection = JDBCUtil.getConn();
        long rowsUpdated = -1;
        try {
            if (connection != null) {
                // 插入到user表
                String sql = "UPDATE Appointment SET order_state = ? WHERE appointment_id = ?";
                PreparedStatement ps = connection.prepareStatement(sql);
                if (ps != null) {
                    ps.setInt(1, appointment.getOrderState());
                    ps.setInt(2, appointment.getAppointmentId());
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

}
