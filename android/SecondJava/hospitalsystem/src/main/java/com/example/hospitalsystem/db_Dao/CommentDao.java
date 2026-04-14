package com.example.hospitalsystem.db_Dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.hospitalsystem.db_Entity.Appointment;
import com.example.hospitalsystem.db_Entity.Comment;
import com.example.hospitalsystem.db_Entity.Patient;
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

public class CommentDao {

    private static final String TAG = "mysql-hospitalsystem-JDBCUtil";

    // 根据患者id删除评论
    // input: username
    // return：是否成功删除
    public long deleteByPatientId(String username) {
        Connection connection = JDBCUtil.getConn();
        long rowsAffected  = -1;
        try {
            if (connection != null) {
                // 插入到user表
                String sql = "DELETE FROM Comment WHERE patient_id = ?";
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

    // 根据评论idd删除评论
    // input: comment_id
    // return：是否成功删除
    public long deleteById(int comment_id) {
        Connection connection = JDBCUtil.getConn();
        long rowsAffected  = -1;
        try {
            if (connection != null) {
                // 插入到user表
                String sql = "DELETE FROM Comment WHERE comment_id = ?";
                PreparedStatement ps = connection.prepareStatement(sql);
                if (ps != null) {
                    ps.setInt(1, comment_id);
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


    // 新增评论记录
    // input: Comment
    // return: 自增主键值 成功(>0) 失败(-1)
    public long insert(Comment comment) {
        Connection connection = JDBCUtil.getConn();
        long generatedId = -1;
        try {
            if (connection != null) {
                // 开启事务
                connection.setAutoCommit(false);
                // 插入到user表
                String sql = "insert into Comment (patient_id, doctor_id, publish_time, " +
                        "comment_text, audit) values (?, ?, ?, ?, ?)";
                PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, comment.getPublisherId());
                ps.setString(2, comment.getCommentedId());
                ps.setTimestamp(3, new Timestamp(comment.getPublishTime().getTime()));
                ps.setString(4, comment.getCommentText());
                ps.setInt(5, comment.getAudit());
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

    // 评价社区，获取所有审核通过的评论记录
    // input: 无
    // return：List<Comment>
    public List<Comment> searchPassed() {
        List<Comment> commentList = new ArrayList<>();
        Connection connection = JDBCUtil.getConn();
        int msg = 0;
        try {
            String sql = "select * from Comment where audit = ?";
            if (connection != null) {
                PreparedStatement ps = connection.prepareStatement(sql);
                if (ps != null) {
                    ps.setInt(1, Comment.PASSED);
                    ResultSet rs = ps.executeQuery();
                    while (rs.next()) { // 检查是否有结果
                        Comment comment = new Comment();
                        int comment_id = rs.getInt("comment_id");
                        String patient_id = rs.getString("patient_id");
                        String doctor_id = rs.getString("doctor_id");
                        Date publish_time = rs.getTimestamp("publish_time");
                        String comment_text = rs.getString("comment_text");
                        int audit = rs.getInt("audit");

                        comment.setCommentId(comment_id);
                        comment.setCommentedId(doctor_id);
                        comment.setPublisherId(patient_id);
                        comment.setPublishTime(publish_time);
                        comment.setCommentText(comment_text);
                        comment.setAudit(audit);

                        commentList.add(comment);
                    }
                    rs.close();
                    ps.close();
                    return commentList;
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
        return commentList;
    }

    // 根据科室id和医生姓名模糊查询评论信息
    // input: 无
    // return：List<Comment>
    public List<Comment> searchByOfficeIdAndDoctorName(int office_id, String doctor_name) {
        List<Comment> commentList = new ArrayList<>();
        Connection connection = JDBCUtil.getConn();
        int msg = 0;
        try {
            String sql = "select * from Comment where doctor_id in " +
                    "(select doctor_id from doctor_infos where name like ? and " +
                    "(office_id = ? or ? = -1))";
            if (connection != null) {
                PreparedStatement ps = connection.prepareStatement(sql);
                if (ps != null) {
                    ps.setString(1, "%" + doctor_name + "%");
                    ps.setInt(2, office_id);
                    ps.setInt(3, office_id);
                    ResultSet rs = ps.executeQuery();
                    while (rs.next()) { // 检查是否有结果
                        Comment comment = new Comment();
                        int comment_id = rs.getInt("comment_id");
                        String patient_id = rs.getString("patient_id");
                        String doctor_id = rs.getString("doctor_id");
                        Date publish_time = rs.getTimestamp("publish_time");
                        String comment_text = rs.getString("comment_text");
                        int audit = rs.getInt("audit");

                        comment.setCommentId(comment_id);
                        comment.setCommentedId(doctor_id);
                        comment.setPublisherId(patient_id);
                        comment.setPublishTime(publish_time);
                        comment.setCommentText(comment_text);
                        comment.setAudit(audit);

                        commentList.add(comment);
                    }
                    rs.close();
                    ps.close();
                    return commentList;
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
        return commentList;
    }

    // 我的评价，根据患者用户名查询评论
    // input: String patient_id
    // return：List<Comment>
    public List<Comment> searchByPatientId(String patient_id) {
        List<Comment> commentList = new ArrayList<>();
        Connection connection = JDBCUtil.getConn();
        int msg = 0;
        try {
            String sql = "select * from Comment where patient_id = ?";
            if (connection != null) {
                PreparedStatement ps = connection.prepareStatement(sql);
                if (ps != null) {
                    ps.setString(1, patient_id);
                    ResultSet rs = ps.executeQuery();
                    while (rs.next()) { // 检查是否有结果
                        Comment comment = new Comment();
                        int comment_id = rs.getInt("comment_id");
                        String doctor_id = rs.getString("doctor_id");
                        Date publish_time = rs.getTimestamp("publish_time");
                        String comment_text = rs.getString("comment_text");
                        int audit = rs.getInt("audit");

                        comment.setCommentId(comment_id);
                        comment.setCommentedId(doctor_id);
                        comment.setPublisherId(patient_id);
                        comment.setPublishTime(publish_time);
                        comment.setCommentText(comment_text);
                        comment.setAudit(audit);

                        commentList.add(comment);
                    }
                    rs.close();
                    ps.close();
                    return commentList;
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
        return commentList;
    }

    // 根据评论者id、医生姓名、发布日期、科室id和审核状态查询
    // input: String patient_id
    // return：List<Comment>
    public List<Comment> searchByPatientId(String patient_id, int audit, String doctor_name, int office_id, String date) {
        List<Comment> commentList = new ArrayList<>();
        Connection connection = JDBCUtil.getConn();
        StringBuilder sql =  new StringBuilder("select * from Comment where patient_id = ? and doctor_id in " +
                "(select doctor_id from doctor_infos where name like ? and " +
                "(office_id = ? or ? = -1)) and (audit = ? or ? = 0)");

        // 如果 date 不为空，则添加 schedule_date 条件
        if (date != null && !date.isEmpty()) {
            sql.append(" and DATE(publish_time) = ?");
        }

        try {
            if (connection != null) {
                PreparedStatement ps = connection.prepareStatement(sql.toString());
                if (ps != null) {
                    ps.setString(1, patient_id);
                    ps.setString(2, "%" + doctor_name + "%");
                    ps.setInt(3, office_id);
                    ps.setInt(4, office_id);
                    ps.setInt(5, audit);
                    ps.setInt(6, audit);

                    if (date != null && !date.isEmpty()) {
                        ps.setString(7, date);
                    }

                    ResultSet rs = ps.executeQuery();
                    while (rs.next()) { // 检查是否有结果
                        Comment comment = new Comment();
                        int comment_id = rs.getInt("comment_id");
                        int audit_ = rs.getInt("audit");
                        String doctor_id = rs.getString("doctor_id");
                        Date publish_time = rs.getTimestamp("publish_time");
                        String comment_text = rs.getString("comment_text");

                        comment.setCommentId(comment_id);
                        comment.setCommentedId(doctor_id);
                        comment.setPublisherId(patient_id);
                        comment.setPublishTime(publish_time);
                        comment.setCommentText(comment_text);
                        comment.setAudit(audit_);

                        commentList.add(comment);
                    }
                    rs.close();
                    ps.close();
                    return commentList;
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
        return commentList;
    }

}
