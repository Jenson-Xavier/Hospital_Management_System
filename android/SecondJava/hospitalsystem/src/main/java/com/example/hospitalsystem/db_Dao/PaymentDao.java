package com.example.hospitalsystem.db_Dao;

import com.example.hospitalsystem.db_Entity.Comment;
import com.example.hospitalsystem.db_Entity.Payment;
import com.example.hospitalsystem.util.JDBCUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PaymentDao {

    private static final String TAG = "mysql-hospitalsystem-JDBCUtil";

    // 根据患者id删除支付记录
    // input: username
    // return：是否成功删除
    public long deleteByPatientId(String username) {
        Connection connection = JDBCUtil.getConn();
        long rowsAffected  = -1;
        try {
            if (connection != null) {
                // 插入到user表
                String sql = "DELETE FROM Payment WHERE patient_id = ?";
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

    // 新增支付记录
    // input: Payment
    // return: 自增主键值 成功(>0) 失败(-1)
    public long insert(Payment payment) {
        Connection connection = JDBCUtil.getConn();
        long generatedId = -1;
        try {
            if (connection != null) {
                // 开启事务
                connection.setAutoCommit(false);
                // 插入到user表
                String sql = "insert into Payment (appointment_id, patient_id, payment_amount, " +
                        "pay_time) values (?, ?, ?, ?)";
                PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                ps.setInt(1, payment.getAppointmentId());
                ps.setString(2, payment.getPayerId());
                ps.setFloat(3, payment.getPaymentAmount());
                ps.setTimestamp(4, new Timestamp(payment.getPayTime().getTime()));
                long success = ps.executeUpdate();

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

    // 根据预约记录id查询支付记录
    // input: int appointment_id
    // return: Payment
    public Payment searchByAppointmentId(int appointment_id) {
        Payment payment = null;
        Connection connection = JDBCUtil.getConn();
        String sql = "select * from Payment where appointment_id = ?";

        try {
            if (connection != null) {
                // 插入到user表
                PreparedStatement ps = connection.prepareStatement(sql);
                ps.setInt(1, appointment_id);
                ResultSet rs = ps.executeQuery();
                // 遍历结果集
                if (rs.next()) {
                    int payment_id = rs.getInt("payment_id");
                    String patient_id = rs.getString("patient_id");
                    float payment_amount = rs.getFloat("payment_amount");
                    java.util.Date pay_time = rs.getTimestamp("pay_time");

                    payment = new Payment();
                    payment.setPaymentId(payment_id);
                    payment.setAppointmentId(appointment_id);
                    payment.setPayerId(patient_id);
                    payment.setPaymentAmount(payment_amount);
                    payment.setPayTime(pay_time);
                }

                // 关闭资源
                rs.close();
                ps.close();
                return payment;
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
        return payment;
    }

    // 根据患者用户名查询支付记录
    // input: String patient_id
    // return: List<Payment>
    public List<Payment> searchByPatientId(String patient_id) {
        List<Payment> paymentList = new ArrayList<>();
        Connection connection = JDBCUtil.getConn();
        String sql = "select * from Payment where patient_id = ?";

        try {
            if (connection != null) {
                // 插入到user表
                PreparedStatement ps = connection.prepareStatement(sql);
                ps.setString(1, patient_id);
                ResultSet rs = ps.executeQuery();
                // 遍历结果集
                while (rs.next()) {
                    int payment_id = rs.getInt("payment_id");
                    int appointment_id = rs.getInt("appointment_id");
                    float payment_amount = rs.getFloat("payment_amount");
                    java.util.Date pay_time = rs.getTimestamp("pay_time");

                    Payment payment = new Payment();
                    payment.setPaymentId(payment_id);
                    payment.setAppointmentId(appointment_id);
                    payment.setPayerId(patient_id);
                    payment.setPaymentAmount(payment_amount);
                    payment.setPayTime(pay_time);

                    paymentList.add(payment);
                }

                // 关闭资源
                rs.close();
                ps.close();
                return paymentList;
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
        return paymentList;
    }

    // 根据患者用户名、支付日期和科室id查询
    // input: String patient_id, int office_id, String date
    // return：List<Payment>
    public List<Payment> searchByPatientIdAndOfficeId(String patient_id, int office_id, String date) {
        List<Payment> paymentList = new ArrayList<>();
        Connection connection = JDBCUtil.getConn();
        StringBuilder sql =  new StringBuilder("select * from Payment where patient_id = ? and appointment_id in " +
                "(select appointment_id from Appointment where doctor_id in " +
                "(select doctor_id from doctor_infos where (office_id = ? or ? = -1)))");

        // 如果 date 不为空，则添加 schedule_date 条件
        if (date != null && !date.isEmpty()) {
            sql.append(" and DATE(pay_time) = ?");
        }

        try {
            if (connection != null) {
                PreparedStatement ps = connection.prepareStatement(sql.toString());
                if (ps != null) {
                    ps.setString(1, patient_id);
                    ps.setInt(2, office_id);
                    ps.setInt(3, office_id);

                    if (date != null && !date.isEmpty()) {
                        ps.setString(4, date);
                    }

                    ResultSet rs = ps.executeQuery();
                    while (rs.next()) { // 检查是否有结果
                        int payment_id = rs.getInt("payment_id");
                        int appointment_id = rs.getInt("appointment_id");
                        float payment_amount = rs.getFloat("payment_amount");
                        java.util.Date pay_time = rs.getTimestamp("pay_time");

                        Payment payment = new Payment();
                        payment.setPaymentId(payment_id);
                        payment.setAppointmentId(appointment_id);
                        payment.setPayerId(patient_id);
                        payment.setPaymentAmount(payment_amount);
                        payment.setPayTime(pay_time);

                        paymentList.add(payment);
                    }
                    rs.close();
                    ps.close();
                    return paymentList;
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
        return paymentList;
    }

}
