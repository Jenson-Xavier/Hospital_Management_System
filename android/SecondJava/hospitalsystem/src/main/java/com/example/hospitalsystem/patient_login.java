package com.example.hospitalsystem;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.hospitalsystem.database.HospitalSysDatabase;
import com.example.hospitalsystem.db_Dao.PatientDao;
import com.example.hospitalsystem.db_Entity.Patient;
import com.example.hospitalsystem.util.EmailUtil;
import com.example.hospitalsystem.util.ToastUtil;
import com.example.hospitalsystem.util.verifyUtil;

public class patient_login extends AppCompatActivity implements RadioGroup.OnCheckedChangeListener, View.OnClickListener {

    private RadioButton rb_account;
    private RadioButton rb_verifycode;
    private RadioGroup rg_login_way;
    private TextView tv_account;
    private TextView tv_password;
    private Button btn_login;
    private Button btn_sendverifycode;
    private TextView tv_forget;
    private TextView tv_signup;
    private EditText et_password;
    private HospitalSysDatabase hospitalSysDB;
    private EditText et_account;

    private Context context;
    private String gt_verifyCode = null;
    // 考虑到短信验证码API无法调用
    private String preset_verifyCode = "123123";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_patient_login);
        context = this;

        rb_account = findViewById(R.id.rb_account);
        rb_verifycode = findViewById(R.id.rb_verifycode);
        rg_login_way = findViewById(R.id.rg_login_way);
        tv_account = findViewById(R.id.tv_account);
        tv_password = findViewById(R.id.tv_password);
        tv_forget = findViewById(R.id.tv_forget);
        tv_signup = findViewById(R.id.tv_signup);
        btn_login = findViewById(R.id.btn_login);
        btn_sendverifycode = findViewById(R.id.btn_sendverifycode);
        et_password = findViewById(R.id.et_password);
        et_account = findViewById(R.id.et_account);

        // rg
        rg_login_way.setOnCheckedChangeListener(this);
        // btn
        btn_login.setOnClickListener(this);
        btn_sendverifycode.setOnClickListener(this);
        // tv
        tv_forget.setOnClickListener(this);
        tv_signup.setOnClickListener(this);

        hospitalSysDB = MyApplication.getInstance().getHospitalDB();
    }

    // rg
    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int i) {
        if (i == rb_account.getId()) {
            btn_sendverifycode.setVisibility(View.GONE);
            tv_password.setText("密码:        ");
            et_password.setHint("请输入密码");
        } else if (i == rb_verifycode.getId()) {
            btn_sendverifycode.setVisibility(View.VISIBLE);
            tv_password.setText("验证码:    ");
            et_password.setHint("请输入验证码");
        }
    }

    // btn
    @Override
    public void onClick(View v) {
        // 登录
        if (v.getId() == R.id.btn_login) {
            // 账号登录
            if (rb_account.isChecked()) {
                String user_name = et_account.getText().toString();
                String password = et_password.getText().toString();
                PatientDao patientDao = new PatientDao();
                // 异步访问远程数据库
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        // 根据用户名查询用户信息
                        Patient patient = patientDao.searchById(user_name);
                        if (patient != null && password.equals(patient.getPassword())) {
                            // 返回主线程执行
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    ToastUtil.show(context, "登录成功!");
                                    // 跳转到首页
                                    Intent intent = new Intent(context, patient_index.class);
                                    // 将用户名传入下一个页面
                                    intent.putExtra("user_name", user_name);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                }
                            });
                        } else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    ToastUtil.show(context, "用户名或密码输入错误!");
                                }
                            });
                        }
                    }
                }).start();
            }
            // 验证码登录
            else {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String user_name = et_account.getText().toString();
                        String verifyCode = et_password.getText().toString();
                        PatientDao patientDao = new PatientDao();
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                // 根据用户名查询用户信息
                                Patient patient = patientDao.searchById(user_name);
                                String true_verifyCode = null;
                                if (verifyUtil.isEmail(user_name)) {
                                    true_verifyCode = gt_verifyCode;
                                } else if (verifyUtil.isPhoneNumber(user_name)) {
                                    true_verifyCode = preset_verifyCode;
                                }
                                if (patient != null && verifyCode.equals(true_verifyCode)) {
                                    // 返回主线程执行
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            ToastUtil.show(context, "登录成功!");
                                            // 跳转到首页
                                            Intent intent = new Intent(context, patient_index.class);
                                            // 将用户名传入下一个页面
                                            intent.putExtra("user_name", user_name);
                                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                            startActivity(intent);
                                        }
                                    });
                                } else {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            ToastUtil.show(context, "用户名或验证码输入错误!");
                                        }
                                    });
                                }
                            }
                        }).start();
                    }
                }).start();
            }

        }
        // 发送验证码
        else if (v.getId() == R.id.btn_sendverifycode) {
            String user_name = et_account.getText().toString();
            // 判断用户名是手机号还是邮箱号
            if (verifyUtil.isEmail(user_name)) {
                gt_verifyCode = EmailUtil.randomVerify();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        EmailUtil.sendMail(user_name, gt_verifyCode);
                    }
                }).start();
                btn_sendverifycode.setEnabled(false);
                // 使用 CountDownTimer 实现倒计时
                new CountDownTimer(10 * 1000, 1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                        // 每秒更新按钮上的文字
                        btn_sendverifycode.setText("重新发送: " + millisUntilFinished / 1000 + "秒");
                    }

                    @Override
                    public void onFinish() {
                        // 倒计时结束，恢复按钮状态
                        btn_sendverifycode.setEnabled(true);
                        btn_sendverifycode.setText("发送验证码");
                    }
                }.start();
            } else if (verifyUtil.isPhoneNumber(user_name)) {
                btn_sendverifycode.setEnabled(false);
                // 使用 CountDownTimer 实现倒计时
                new CountDownTimer(10 * 1000, 1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {
                        // 每秒更新按钮上的文字
                        btn_sendverifycode.setText("重新发送: " + millisUntilFinished / 1000 + "秒");
                    }

                    @Override
                    public void onFinish() {
                        // 倒计时结束，恢复按钮状态
                        btn_sendverifycode.setEnabled(true);
                        btn_sendverifycode.setText("发送验证码");
                    }
                }.start();
            } else {
                ToastUtil.show(context, "用户名非合法手机号或邮箱格式");
            }
        }
        // 忘记密码
        else if (v.getId() == R.id.tv_forget) {
            Intent intent = new Intent(this, patient_getback_password.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
        // 注册账号
        else if (v.getId() == R.id.tv_signup) {
            Intent intent = new Intent(this, patient_signup.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
    }

}