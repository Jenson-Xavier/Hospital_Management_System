package com.example.hospitalsystem;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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

public class patient_update_password extends AppCompatActivity implements RadioGroup.OnCheckedChangeListener, View.OnClickListener {

    private RadioGroup rg_updatepwd_way;
    private RadioButton rb_old_pwd;
    private RadioButton rb_verifycode;
    private TextView tv_old_password;
    private EditText et_old_password;
    private EditText et_new_password;
    private EditText et_new_password_again;
    private Button btn_sendverifycode;
    private Button btn_confirm;
    private ImageView iv_back;

    private static String mVerifycode = "111111";
    private HospitalSysDatabase hospitalSysDB;
    private String user_name;

    private String gt_verifyCode = null;
    // 考虑到短信验证码API无法调用
    private String preset_verifyCode = "123123";
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_patient_update_password);

        rg_updatepwd_way = findViewById(R.id.rg_updatepwd_way);
        rb_old_pwd = findViewById(R.id.rb_old_pwd);
        rb_verifycode = findViewById(R.id.rb_verifycode);
        tv_old_password = findViewById(R.id.tv_old_password);
        et_old_password = findViewById(R.id.et_old_password);
        et_new_password = findViewById(R.id.et_new_password);
        et_new_password_again = findViewById(R.id.et_new_password_again);
        btn_sendverifycode = findViewById(R.id.btn_sendverifycode);
        btn_confirm = findViewById(R.id.btn_confirm);
        iv_back = findViewById(R.id.iv_back);

        TextView tv_title = findViewById(R.id.tv_title);
        tv_title.setText("修改密码");

        // rg
        rg_updatepwd_way.setOnCheckedChangeListener(this);
        // btn
        btn_sendverifycode.setOnClickListener(this);
        btn_confirm.setOnClickListener(this);
        // iv
        iv_back.setOnClickListener(this);

        Intent intent = getIntent();
        user_name = intent.getStringExtra("user_name");

        hospitalSysDB = MyApplication.getInstance().getHospitalDB();
        context = this;
    }

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

    // radiogroup
    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int i) {
        // 原密码方式登录
        if (i == rb_old_pwd.getId()) {
            btn_sendverifycode.setVisibility(View.GONE);
            tv_old_password.setText("原密码:    ");
            et_old_password.setText("");
            et_old_password.setHint("请输入原密码");
            et_new_password_again.setText("");
            et_new_password.setText("");
        }
        // 验证码方式登录
        else if (i == rb_verifycode.getId()) {
            btn_sendverifycode.setVisibility(View.VISIBLE);
            tv_old_password.setText("验证码:    ");
            et_old_password.setText("");
            et_old_password.setHint("请输入验证码");
            et_new_password_again.setText("");
            et_new_password.setText("");
        }
    }


    @Override
    public void onClick(View v) {
        // 确认修改
        if (v.getId() == R.id.btn_confirm) {
            String new_password = et_new_password.getText().toString();
            String new_password_again = et_new_password_again.getText().toString();
            // 检验密码输入是否一致
            if (!new_password_again.equals(new_password)) {
                ToastUtil.show(this, "设置失败，两次密码输入不一致");
                return;
            }
            // 检验密码强弱性
            if (!checkPwd(new_password)) {
                ToastUtil.show(this, "设置失败，密码未通过强弱性检验");
                return;
            }
            new Thread(new Runnable() {
                @Override
                public void run() {
                    PatientDao patientDao = new PatientDao();
                    Patient patient = patientDao.searchById(user_name);
                    // 原密码方式修改
                    if (rb_old_pwd.isChecked()) {
                        String old_password = et_old_password.getText().toString();
                        // 检验原密码是否正确
                        if (patient != null && !old_password.equals(patient.getPassword())) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    ToastUtil.show(context, "修改失败，原密码输入错误");
                                }
                            });
                            return;
                        }
                    }
                    // 验证码方式修改
                    else if (rb_verifycode.isChecked()) {
                        String verifyCode = et_old_password.getText().toString();
                        String true_verifyCode = null;
                        if (verifyUtil.isEmail(user_name)) {
                            true_verifyCode = gt_verifyCode;
                        }
                        else if (verifyUtil.isPhoneNumber(user_name)) {
                            true_verifyCode = preset_verifyCode;
                        }
                        // 检验验证码
                        if (!verifyCode.equals(true_verifyCode)) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    ToastUtil.show(context, "设置失败，验证码不正确");
                                }
                            });
                            return;
                        }
                    }
                    if (patient != null) {
                        patient.setPassword(new_password);
                        patient.setUserType(Patient.PATIENT);
                        // 修改用户信息
                        long success = patientDao.update(patient);
                        if (success > 0) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    ToastUtil.show(context, "设置新密码成功!");
                                    finish();
                                }
                            });
                        }
                        else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    ToastUtil.show(context, "密码修改失败!");
                                    finish();
                                }
                            });
                        }
                    }
                }
            }).start();
        }
        // 发送验证码
        else if (v.getId() == R.id.btn_sendverifycode) {
            // 邮箱验证码核验
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
                // 短信验证码核验
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
                ToastUtil.show(this, "用户名非合法手机号或邮箱格式");
            }
        }
        // 返回
        else if (v.getId() == R.id.iv_back) {
            finish();
        }
    }
}