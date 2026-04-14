package com.example.hospitalsystem.Controller;

import com.example.hospitalsystem.Entity.Comment;
import com.example.hospitalsystem.Entity.Doctor;
import com.example.hospitalsystem.Entity.Patient;
import com.example.hospitalsystem.Service.CommentService;
import com.example.hospitalsystem.Service.DoctorService;
import com.example.hospitalsystem.Service.PatientService;
import com.example.hospitalsystem.pojo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/comment")
public class CommentController {

    @Autowired
    private CommentService commentService;

    @Autowired
    private PatientService patientService;

    @Autowired
    private DoctorService doctorService;

    // 获取全部评论
    @GetMapping("/getAll")
    public Result<List<Comment>> getCommentList() {
        List<Comment> commentList = commentService.findAll();
        return Result.success(commentList);
    }

    // 根据评论id删除评论
    @PostMapping("/delete")
    public Result deleteComment(String id) {
        long comment_id = Long.parseLong(id);
        Comment comment = commentService.findById(comment_id);
        if (comment == null) {
            return Result.error("该条评论不存在，无法删除");
        }
        comment.setPublisher(null);
        comment.setCommented(null);
        commentService.deleteById(comment_id);
        return Result.success();
    }

    // 新增评论
    @PostMapping("/add")
    public Result addComment(@RequestParam Map<String, String> map) {
        // 提取参数
        String patient_id = map.get("patient_id");
        String doctor_id = map.get("doctor_id");
        String comment_text = map.get("comment_text");
        if (patient_id == null || doctor_id == null || comment_text == null) {
            return Result.error("患者医生用户名/评论内容为空");
        }

        Comment comment = new Comment();
        // 查询患者
        Patient patient = patientService.findById(patient_id);
        if (patient == null) {
            return Result.error("患者用户不存在");
        }
        comment.setPublisher(patient);
        // 查询医生
        Doctor doctor = doctorService.findById(doctor_id);
        if (doctor == null) {
            return Result.error("医生用户不存在");
        }
        comment.setCommented(doctor);
        comment.setAudit(Comment.WAIT);
        comment.setComment_text(comment_text);
        comment.setPublish_time(new Timestamp(System.currentTimeMillis()));
        commentService.save(comment);
        return Result.success();
    }

    // 审核评论
    @PostMapping("/auditPass")
    public Result auditPass(String id) {
        long comment_id = Long.parseLong(id);
        Comment comment = commentService.findById(comment_id);
        if (comment == null) {
            return Result.error("该评论信息不存在");
        }
        comment.setAudit(Comment.PASSED);
        commentService.save(comment);
        return Result.success();
    }

    // 审核评论
    @PostMapping("/auditUnPass")
    public Result auditUnPass(String id) {
        long comment_id = Long.parseLong(id);
        Comment comment = commentService.findById(comment_id);
        if (comment == null) {
            return Result.error("该评论信息不存在");
        }
        comment.setAudit(Comment.UNPASSED);
        commentService.save(comment);
        return Result.success();
    }
}
