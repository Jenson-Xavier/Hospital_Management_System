package com.example.hospitalsystem.Controller;

import com.example.hospitalsystem.Entity.Admin;
import com.example.hospitalsystem.Entity.Doctor;
import com.example.hospitalsystem.Entity.User;
import com.example.hospitalsystem.Service.AdminService;
import com.example.hospitalsystem.pojo.Result;
import com.example.hospitalsystem.utils.JwtUtil;
import com.example.hospitalsystem.utils.ThreadLocalUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @PostMapping("/login")
    public Result<String> Login(String username, String password) {
        if (!StringUtils.hasLength(username) || !StringUtils.hasLength(password)) {
            return Result.error("用户名和密码不得为空");
        }
        Admin admin = adminService.findById(username);
        if (admin == null) {
            return Result.error("该管理员用户名不存在");
        }
        if (!admin.getPassword().equals(password)) {
            return Result.error("密码错误");
        }
        // 生成token token中携带信息: 用户名 （即患者用户主键）
        Map<String, Object> claims = new HashMap<>();
        claims.put("username", admin.getUser_id());
        String token = JwtUtil.genToken(claims);
        // 把token存储到redis中
        ValueOperations<String, String> operations = stringRedisTemplate.opsForValue();
        operations.set(token, token, 1, TimeUnit.HOURS);    // 过期时间1h
        return Result.success(token);
    }

    // 获取医生用户名
    @GetMapping("/adminId")
    public Result<Admin> getAdminId() {
        Map<String, Object> map = ThreadLocalUtil.get();
        String username = (String) map.get("username");
        Admin admin = adminService.findById(username);
        return Result.success(admin);
    }

}
