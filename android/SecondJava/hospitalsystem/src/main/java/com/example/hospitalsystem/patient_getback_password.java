package com.example.hospitalsystem;

import android.content.Context;
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

public class patient_getback_password extends AppCompatActivity implements RadioGroup.OnCheckedChangeListener, View.OnClickListener {

    private EditText et_phone;
    private EditText et_verifycode;
    private EditText et_new_password;
    private EditText et_new_password_again;
    private Button btn_confirm;
    private RadioGroup rg_getback_way;
    private RadioButton rb_phone;
    private RadioButton rb_email;
    private ImageView iv_back;
    private TextView tv_title;
    private TextView tv_phone;
    private Button btn_sendverifycode;

    private String mVerifycode = "111111";
    private HospitalSysDatabase hospitalSysDB;

    private String gt_verifyCode = null;
    // 考虑到短信验证码API无法调用
    private String preset_verifyCode = "123123";
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_patient_getback_password);

        rg_getback_way = findViewById(R.id.rg_getback_way);
        rb_phone = findViewById(R.id.rb_phone);
        rb_email = findViewById(R.id.rb_email);
        et_phone = findViewById(R.id.et_phone);
        et_verifycode = findViewById(R.id.et_verifycode);
        et_new_password = findViewById(R.id.et_new_password);
        et_new_password_again = findViewById(R.id.et_new_password_again);
        btn_confirm = findViewById(R.id.btn_confirm);
        btn_sendverifycode = findViewById(R.id.btn_sendverifycode);
        iv_back = findViewById(R.id.iv_back);
        tv_title = findViewById(R.id.tv_title);
        tv_phone = findViewById(R.id.tv_phone);
        tv_title.setText("修改密码");

        // rg
        rg_getback_way.setOnCheckedChangeListener(this);
        // btn
        btn_confirm.setOnClickListener(this);
        btn_sendverifycode.setOnClickListener(this);
        // iv
        iv_back.setOnClickListener(this);

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
        if (i == rb_phone.getId()) {
            tv_phone.setText("手机号:    ");
            et_phone.setText("");
            et_phone.setHint("请输入手机号");
        }
        else if (i == rb_email.getId()) {
            tv_phone.setText("邮箱号:    ");
            et_phone.setText("");
            et_phone.setHint("请输入邮箱号");
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_confirm) {
            String phone = et_phone.getText().toString();
            String verifycode = et_verifycode.getText().toString();
            String password = et_new_password.getText().toString();
            String password_again = et_new_password_again.getText().toString();
            String true_verifyCode = null;
            if (rb_email.isChecked()) {
                true_verifyCode = gt_verifyCode;
            }
            else if (rb_phone.isChecked()) {
                true_verifyCode = preset_verifyCode;
            }
            // 检验验证码
            if (!verifycode.equals(true_verifyCode)) {
                ToastUtil.show(this, "设置失败，验证码不正确");
                return;
            }
            // 检验密码输入是否一致
            if (!password_again.equals(password)) {
                ToastUtil.show(this, "设置失败，两次密码输入不一致");
                return;
            }
            // 检验密码强弱性
            if (!checkPwd(password)) {
                ToastUtil.show(this, "设置失败，密码未通过强弱性检验");
                return;
            }
            PatientDao patientDao = new PatientDao();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Patient patient = patientDao.searchById(phone);
                    if (patient == null) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ToastUtil.show(context, "该用户不存在，请重新输入手机号/邮箱号");
                                return;
                            }
                        });
                        return;
                    }
                    patient.setPassword(password);
                    patient.setUserType(Patient.PATIENT);
                    long success = patientDao.update(patient);
                    if (success > 0) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ToastUtil.show(context, "设置新密码成功!");
                                finish();
                            }
                        });
                        return;
                    }
                }
            }).start();
        }
        // 发送验证码
        else if (v.getId() == R.id.btn_sendverifycode) {
            String user_name = et_phone.getText().toString();
            // 邮箱验证码注册
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
                // 短信验证码注册
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