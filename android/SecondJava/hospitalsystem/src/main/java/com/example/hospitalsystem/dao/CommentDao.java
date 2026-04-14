package com.example.hospitalsystem.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.hospitalsystem.entity.Comment;

import java.util.Date;
import java.util.List;

@Dao
public interface CommentDao {

    @Insert
    void insert(Comment comment);

    @Delete
    void delete(Comment comment);

    @Query("SELECT * FROM comments")
    List<Comment> queryAll();

    // 根据患者用户名查询评论
    @Query("SELECT * FROM comments WHERE publisher_id = :user_name")
    List<Comment> queryCommentByPatientId(String user_name);

    // 查询已通过的评论
    @Query("SELECT * FROM comments WHERE audit = 2")
    List<Comment> queryPassedComment();

    // 根据医生姓名和科室名称查询 (部分匹配)
    @Query("SELECT * FROM comments WHERE commented_id IN " +
            "(SELECT doctor_id FROM doctor_infos WHERE name LIKE '%' || :doctor_name || '%' AND " +
            "(office_id = :office_id OR :office_id = -1))")
    List<Comment> queryCommentByDoctorNameAndOfficeId(String doctor_name, int office_id);

    // 插入多条记录
    @Insert
    void insertComments(List<Comment> list);

    // 根据评论者id、医生姓名、发布日期和审核状态查询 (部分匹配)
    @Query("SELECT * FROM comments WHERE publisher_id = :user_name AND date(publish_time / 1000, 'unixepoch') = date(:publish_time / 1000, 'unixepoch') AND " +
            "(audit = :audit OR :audit = 0) AND commented_id IN " +
            "(SELECT doctor_id FROM doctor_infos WHERE name LIKE '%' || :doctor_name || '%' AND (office_id = :office_id OR :office_id = -1))")
    List<Comment> queryCommentByPatientIdAndDoctorNameAndPublishTimeAndStateAndOfficeId(String user_name, Date publish_time, int audit, String doctor_name, int office_id);

    // 根据评论者id、医生姓名和审核状态查询 (部分匹配)
    @Query("SELECT * FROM comments WHERE publisher_id = :user_name AND " +
            "(audit = :audit OR :audit = 0) AND commented_id IN " +
            "(SELECT doctor_id FROM doctor_infos WHERE name LIKE '%' || :doctor_name || '%' AND (office_id = :office_id OR :office_id = -1))")
    List<Comment> queryCommentByPatientIdAndDoctorNameAndStateAndOfficeId(String user_name, int audit, String doctor_name, int office_id);

}
