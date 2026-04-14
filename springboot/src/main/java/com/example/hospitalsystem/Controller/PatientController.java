package com.example.hospitalsystem.Controller;

import com.example.hospitalsystem.Entity.Doctor;
import com.example.hospitalsystem.Entity.Patient;
import com.example.hospitalsystem.Entity.Payment;
import com.example.hospitalsystem.Repository.AppointmentRepository;
import com.example.hospitalsystem.Service.*;
import com.example.hospitalsystem.pojo.Result;
import com.example.hospitalsystem.utils.CodeUtil;
import com.example.hospitalsystem.utils.JwtUtil;
import com.example.hospitalsystem.utils.ThreadLocalUtil;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;


@Slf4j
@RestController
@RequestMapping("/patient")
public class PatientController {

    @Autowired
    private PatientService patientService;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    // 默认验证码为123123 由于手机短信验证码需要企业认证 故进行模拟
    private String pVerifyCode = "123123";

    @Qualifier("redisTemplate")
    @Autowired
    private RedisTemplate redisTemplate;

    // 默认发送方邮件
    @Value("${spring.mail.username}")
    private String from_email;

    @Resource
    private JavaMailSender mailSender;

    // 密码强弱性校验
    private boolean checkPwd(String password) {
        // 长度不得短于8位
        if (password.length() < 8) {
            return false;
        }
        // 须同时包含大小写字母和数字
        boolean hasUpperCase = false;
        boolean hasLowerCase = false;
        boolean hasDigit = false;
        for (char c : password.toCharArray()) {
            if (Character.isUpperCase(c)) {
                hasUpperCase = true;
            } else if (Character.isLowerCase(c)) {
                hasLowerCase = true;
            } else if (Character.isDigit(c)) {
                hasDigit = true;
            }
            if (hasUpperCase && hasLowerCase && hasDigit) {
                return true;
            }
        }
        return false;
    }

    // 注册
    @PostMapping("/register")
    public Result Register(String username, String verifyCode, String password, String rePassword, String register_mode) {
        // 检验输入信息是否为空
        if (!StringUtils.hasLength(username) || !StringUtils.hasLength(verifyCode) || !StringUtils.hasLength(password) || !StringUtils.hasLength(rePassword)) {
            return Result.error("用户名/验证码/密码不得为空");
        }
        // 判断用户是否已经存在
        Patient patient = patientService.findById(username);
        if (patient != null) {
            return Result.error("该患者用户已经存在，无法重复注册");
        }
        // 邮件验证
        if (register_mode.equals("email")) {
            String gt_verifyCode = (String) redisTemplate.opsForValue().get(username);
            if (!StringUtils.hasLength(gt_verifyCode)) {
                return Result.error("验证码已过期，请重新发送");
            }
            // 检验验证码的正确性
            if (!verifyCode.equals(gt_verifyCode)) {
                return Result.error("验证码不正确，请重新输入");
            }
        }
        // 手机验证
        else if (register_mode.equals("phone")) {
            // 检验验证码的正确性
            System.out.println(pVerifyCode);
            if (!verifyCode.equals(pVerifyCode)) {
                return Result.error("验证码不正确，请重新输入");
            }
        }
        // 校验密码强弱性
        if (!checkPwd(password)) {
            return Result.error("密码强弱性检验不通过");
        }
        // 检验两次密码输入是否一致
        if (!password.equals(rePassword)) {
            return Result.error("两次密码输入不一致");
        }
        patient = new Patient();
        // 插入患者用户
        patient.setUser_id(username);
        patient.setPassword(password);
        patient.setUser_type(Patient.PATIENT);
        patientService.save(patient);
        return Result.success();
    }

    @PostMapping("/test")
    public Result TempTest(String tmp) {
        // tmp = "hello";
        return Result.success("成功访问" + tmp);
    }

    // 登录
    @PostMapping("/login")
    public Result<String> Login(String username, String password, String login_mode) {
        if (!StringUtils.hasLength(username) || !StringUtils.hasLength(password)) {
            return Result.error("用户名和密码/验证码不得为空");
        }
        Patient patient = patientService.findById(username);
        if (patient == null) {
            return Result.error("该患者用户名不存在");
        }
        // 1、密码登录方式下
        if (login_mode.equals("password")) {
            if (!patient.getPassword().equals(password)){
                return Result.error("密码错误");
            }
        }
        // 2、邮件验证码登录方式下
        else if (login_mode.equals("code_email")) {
            String verifyCode = (String) redisTemplate.opsForValue().get(username);
            if (!StringUtils.hasLength(verifyCode)) {
                return Result.error("验证码已过期，请重新发送");
            }
            if (!verifyCode.equals(password)) {
                return Result.error("验证码不正确, 请重新发送");
            }
        }
        // 3、短信验证码登录方式下
        else if (login_mode.equals("code_phone")) {
            if (!pVerifyCode.equals(password)) {
                return Result.error("验证码不正确, 请重新发送");
            }
        }
        // 生成token token中携带信息: 用户名 （即患者用户主键）
        Map<String, Object> claims = new HashMap<>();
        claims.put("username", patient.getUser_id());
        String token = JwtUtil.genToken(claims);
        // 把token存储到redis中
        ValueOperations<String, String> operations = stringRedisTemplate.opsForValue();
        operations.set(token, token, 1, TimeUnit.HOURS);    // 过期时间1h
        return Result.success(token);
    }

    // 修改密码
    @PostMapping("/updatePwd")
    public Result UpdatePwd(@RequestParam Map<String, String> params, @RequestHeader("Authorization") String token) {
        // 提取参数
        String verifyCode = params.get("verifyCode");
        String oldPwd = params.get("oldPassword");
        String newPwd = params.get("newPassword");
        String mode = params.get("mode");
        // 从token中提取username
        Map<String, Object> map = ThreadLocalUtil.get();
        String username = (String) map.get("username");
        Patient patient = patientService.findById(username);
        // 原密码方式核验
        if (mode.equals("password")) {
            if (!patient.getPassword().equals(oldPwd)) {
                return Result.error("原密码不正确");
            }
        }
        // 邮箱验证码方式核验
        else if (mode.equals("code_email")) {
            String gt_verifyCode = (String) redisTemplate.opsForValue().get(username);
            if (!StringUtils.hasLength(gt_verifyCode)) {
                return Result.error("验证码已过期，请重新发送");
            }
            if (!verifyCode.equals(gt_verifyCode)) {
                return Result.error("邮箱验证码输入不正确，请重新输入");
            }
        }
        // 手机验证码方式核验
        else if (mode.equals("code_phone")) {
            System.out.println(pVerifyCode);
            if (!verifyCode.equals(pVerifyCode)) {
                return Result.error("短信验证码输入不正确，请重新输入");
            }
        }
        patient.setPassword(newPwd);
        // 借助jpa save接口完成更新操作
        patientService.save(patient);
        // 删除redis中对应的token
        ValueOperations<String, String> operations = stringRedisTemplate.opsForValue();
        operations.getOperations().delete(token);
        return Result.success();
    }

    // 忘记密码
    @PostMapping("/forgetPwd")
    public Result ForgetPwd(/*@RequestBody Map<String, String> params*/String username, String verifyCode, String password, String rePassword, String getBack_mode) {
        // 判断输入是否为空
        if (!StringUtils.hasLength(username) || !StringUtils.hasLength(verifyCode) || !StringUtils.hasLength(password) || !StringUtils.hasLength(rePassword)) {
            return Result.error("输入内容不得为空");
        }
        // 查询用户是否存在
        Patient patient = patientService.findById(username);
        if (patient == null) {
            return Result.error("该患者用户不存在");
        }
        // 验证码正确性判断
        // 邮件验证码
        if (getBack_mode.equals("email")) {
            String gt_verifyCode = (String) redisTemplate.opsForValue().get(username);
            if (!StringUtils.hasLength(gt_verifyCode)) {
                return Result.error("验证码已过期，请重新发送");
            }
            if (!verifyCode.equals(gt_verifyCode)) {
                return Result.error("验证码不正确, 请重新输入");
            }
        }
        // 短信验证码
        else if (getBack_mode.equals("phone")) {
            if (!verifyCode.equals(pVerifyCode)) {
                return Result.error("验证码不正确, 请重新输入");
            }
        }
        // 密码强弱性校验
        if (!checkPwd(password)) {
            return Result.error("密码强弱性检验不通过");
        }
        // 检验两次密码输入是否一致
        if (!rePassword.equals(password)) {
            return Result.error("两次密码输入不一致");
        }
        // 更新密码
        patient.setPassword(password);
        patientService.save(patient);
        // 删除redis中对应的token 待实现
        return Result.success(username);
    }

    // 发送邮箱验证码
    @PostMapping("/sendEmail")
    public Result sendEmail(String email) {
        // 获取随机6位验证码
        String verifyCode = CodeUtil.randomVerify();
        // 将验证码放入redis中    有效期设置为5min
        redisTemplate.opsForValue().set(email, verifyCode, 5, TimeUnit.MINUTES);
        SimpleMailMessage message = getSimpleMailMessage(email, verifyCode);
        try {
            // 发送邮件
            mailSender.send(message);
            return Result.success();
        } catch (Exception e) {
            return Result.error("验证码系统发送失败，请检查邮箱账号是否正确！");
        }
    }

    private SimpleMailMessage getSimpleMailMessage(String email, String verify) {
        SimpleMailMessage message = new SimpleMailMessage();
        // 发送邮箱
        message.setFrom(from_email);
        // 接受邮箱
        message.setTo(email);
        // 标题
        message.setSubject("479医院预约挂号系统-邮箱验证");
        // 内容
        message.setText("尊敬的用户您好：" +
                "您正在进行邮箱验证，本次验证码为：" + verify + "，请在5分钟内进行使用。" +
                "如非本人操作，请忽略此邮件，由此给您带来的不便请谅解！--医院预约挂号平台");
        return message;
    }


    // 获取患者用户信息
    @GetMapping("/patientInfo")
    public Result<Patient> getPatientInfo(/*@RequestHeader(name = "Authorization", required = false) String token*/) {
         // Map<String, Object> map = JwtUtil.parseToken(token);
         // String username = (String) map.get("username");
        Map<String, Object> map = ThreadLocalUtil.get();
        String username = (String) map.get("username");
        Patient patient = patientService.findById(username);
        return Result.success(patient);
    }

    // 获取全部患者用户信息
    @GetMapping("/getAll")
    public Result<List<Patient>> getAllPatient() {
        List<Patient> patientList = patientService.findAll();
        return Result.success(patientList);
    }

    // 注销患者用户(管理员)
    @PostMapping("/del")
    public Result delPatient(String id) {
        // 查询依赖实体并进行删除
        // 查询预约记录
        patientService.deleteById(id);
        return Result.success();
    }

    // 注销患者账户(患者用户本人)
    // 修改密码
    @PostMapping("/delMy")
    public Result deleteMyAccount(@RequestParam Map<String, String> params, @RequestHeader("Authorization") String token) {
        // 提取参数
        String verifyCode = params.get("verifyCode");
        String oldPwd = params.get("oldPassword");
        String mode = params.get("mode");
        // 从token中提取username
        Map<String, Object> map = ThreadLocalUtil.get();
        String username = (String) map.get("username");
        Patient patient = patientService.findById(username);
        // 原密码方式核验
        if (mode.equals("password")) {
            if (!patient.getPassword().equals(oldPwd)) {
                return Result.error("原密码不正确");
            }
        }
        // 邮箱验证码方式核验
        else if (mode.equals("code_email")) {
            String gt_verifyCode = (String) redisTemplate.opsForValue().get(username);
            if (!StringUtils.hasLength(gt_verifyCode)) {
                return Result.error("验证码已过期，请重新发送");
            }
            if (!verifyCode.equals(gt_verifyCode)) {
                return Result.error("邮箱验证码输入不正确，请重新输入");
            }
        }
        // 手机验证码方式核验
        else if (mode.equals("code_phone")) {
            System.out.println(pVerifyCode);
            if (!verifyCode.equals(pVerifyCode)) {
                return Result.error("短信验证码输入不正确，请重新输入");
            }
        }
        // 借助jpa save接口完成删除操作
        patientService.delete(patient);
        // 删除redis中对应的token
        ValueOperations<String, String> operations = stringRedisTemplate.opsForValue();
        operations.getOperations().delete(token);
        return Result.success();
    }

}
