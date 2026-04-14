package com.example.hospitalsystem.Controller;

import com.example.hospitalsystem.Entity.Doctor;
import com.example.hospitalsystem.Entity.DoctorInfo;
import com.example.hospitalsystem.Entity.Patient;
import com.example.hospitalsystem.Entity.User;
import com.example.hospitalsystem.Service.DoctorService;
import com.example.hospitalsystem.pojo.Result;
import com.example.hospitalsystem.utils.JwtUtil;
import com.example.hospitalsystem.utils.ThreadLocalUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.print.Doc;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/doctor")
public class DoctorController {

    @Autowired
    private DoctorService doctorService;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @PostMapping("/login")
    public Result<String> Login(String username, String password) {
        if (!StringUtils.hasLength(username) || !StringUtils.hasLength(password)) {
            return Result.error("用户名和密码不得为空");
        }
        Doctor doctor = doctorService.findById(username);
        if (doctor == null) {
            return Result.error("该医生用户名不存在");
        }
        if (!doctor.getPassword().equals(password)) {
            return Result.error("密码错误");
        }
        // 生成token token中携带信息: 用户名 （即患者用户主键）
        Map<String, Object> claims = new HashMap<>();
        claims.put("username", doctor.getUser_id());
        String token = JwtUtil.genToken(claims);
        // 把token存储到redis中
        ValueOperations<String, String> operations = stringRedisTemplate.opsForValue();
        operations.set(token, token, 1, TimeUnit.HOURS);    // 过期时间1h
        return Result.success(token);
    }

    // 获取医生用户名
    @GetMapping("/doctorId")
    public Result<Doctor> getDoctorId() {
        Map<String, Object> map = ThreadLocalUtil.get();
        String username = (String) map.get("username");
        Doctor doctor = doctorService.findById(username);
        return Result.success(doctor);
    }

    // 修改密码
    @PostMapping("/updatePwd")
    public Result UpdatePwd(@RequestParam Map<String, String> params, @RequestHeader("Authorization") String token) {
        System.out.println(params);
        // 检验参数
        String newPwd = params.get("newPassword");
        String oldPwd = params.get("oldPassword");
        // 从token中提取username
        Map<String, Object> map = ThreadLocalUtil.get();
        String username = (String) map.get("username");
        Doctor doctor = doctorService.findById(username);
        if (!doctor.getPassword().equals(oldPwd)) {
            return Result.error("原密码不正确");
        }
        doctor.setPassword(newPwd);
        // 借助jpa save接口完成更新操作
        doctorService.save(doctor);
        // 删除redis中对应的token
        ValueOperations<String, String> operations = stringRedisTemplate.opsForValue();
        operations.getOperations().delete(token);
        return Result.success();
    }

    // 添加医生账户
    @PostMapping("/add")
    public Result Add(@RequestParam Map<String, String> params) {
        // 提取参数
        String user_id = params.get("user_id");
        String password = params.get("password");
        String rePassword = params.get("rePassword");

        Doctor doctor = doctorService.findById(user_id);
        if (doctor != null) {
            return Result.error("该医生账户已经存在，无法重复添加");
        }
        doctor = new Doctor();
        doctor.setUser_id(user_id);
        doctor.setPassword(password);
        doctor.setUser_type(User.DOCTOR);
        // 添加医生账户
        doctorService.save(doctor);
        return Result.success();
    }

    // 获取全部医生账户
    @GetMapping("/getAll")
    public Result<List<Doctor>> getAllDoctor() {
        List<Doctor> doctorList = doctorService.findAll();
        return Result.success(doctorList);
    }

    // 注销医生用户
    @PostMapping("/del")
    public Result delPatient(String id) {
        doctorService.deleteById(id);
        return Result.success();
    }

}
