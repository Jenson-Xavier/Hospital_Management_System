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
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.hospitalsystem.database.HospitalSysDatabase;
import com.example.hospitalsystem.db_Dao.PatientDao;
import com.example.hospitalsystem.db_Entity.Patient;
import com.example.hospitalsystem.util.EmailUtil;
import com.example.hospitalsystem.util.ToastUtil;
import com.example.hospitalsystem.util.verifyUtil;

public class patient_del_account extends AppCompatActivity implements RadioGroup.OnCheckedChangeListener, View.OnClickListener {

    private RadioGroup rg_updatepwd_way;
    private RadioButton rb_old_pwd;
    private RadioButton rb_verifycode;
    private ImageView iv_back;
    private TextView tv_old_password;
    private EditText et_old_password;
    private Button btn_sendverifycode;
    private Button btn_signout;
    private String user_name;
    private HospitalSysDatabase hospitalSysDB;

    private final String mVerifycode = "111111";

    private String gt_verifyCode = null;
    // 考虑到短信验证码API无法调用
    private String preset_verifyCode = "123123";
    private Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_patient_del_account);

        rg_updatepwd_way = findViewById(R.id.rg_signout_way);
        rb_old_pwd = findViewById(R.id.rb_old_pwd);
        rb_verifycode = findViewById(R.id.rb_verifycode);
        iv_back = findViewById(R.id.iv_back);
        tv_old_password = findViewById(R.id.tv_old_password);
        et_old_password = findViewById(R.id.et_old_password);
        btn_sendverifycode = findViewById(R.id.btn_sendverifycode);
        btn_signout = findViewById(R.id.btn_signout);

        TextView tv_title = findViewById(R.id.tv_title);
        tv_title.setText("注销账户");

        // rg
        rg_updatepwd_way.setOnCheckedChangeListener(this);
        // btn
        btn_sendverifycode.setOnClickListener(this);
        btn_signout.setOnClickListener(this);
        // iv
        iv_back.setOnClickListener(this);

        Intent intent = getIntent();
        user_name = intent.getStringExtra("user_name");

        hospitalSysDB = MyApplication.getInstance().getHospitalDB();
        context = this;
    }

    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int i) {
        // 原密码方式注销
        if (i == rb_old_pwd.getId()) {
            btn_sendverifycode.setVisibility(View.GONE);
            tv_old_password.setText("原密码:    ");
            et_old_password.setText("");
            et_old_password.setHint("请输入原密码");
        }
        // 验证码方式注销
        else if (i == rb_verifycode.getId()) {
            btn_sendverifycode.setVisibility(View.VISIBLE);
            tv_old_password.setText("验证码:    ");
            et_old_password.setText("");
            et_old_password.setHint("请输入验证码");
        }
    }

    @Override
    public void onClick(View v) {
        // 注销
        if (v.getId() == R.id.btn_signout) {
            // 原密码方式
            if (rb_old_pwd.isChecked()) {
                String old_password = et_old_password.getText().toString();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        PatientDao patientDao = new PatientDao();
                        Patient patient = patientDao.searchById(user_name);
                        // 检查原密码是否正确
                        if (patient != null && old_password.equals(patient.getPassword())) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                    builder.setTitle("确认注销");
                                    builder.setMessage("尊敬的用户 " + user_name + "\n您确定要注销账户吗?");
                                    builder.setPositiveButton("是", (dialog, which)->{
                                        new Thread(new Runnable() {
                                            @Override
                                            public void run() {
                                                long success = patientDao.delete(user_name);
                                                // 删除患者用户
                                                if (success > 0) {
                                                    runOnUiThread(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            ToastUtil.show(context, "注销成功");
                                                            // 跳转到登录界面
                                                            Intent intent = new Intent(context, patient_login.class);
                                                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                                            startActivity(intent);
                                                        }
                                                    });
                                                } else {
                                                    runOnUiThread(()-> ToastUtil.show(context, "注销失败"));
                                                }
                                            }
                                        }).start();
                                    });
                                    builder.setNegativeButton("否", null);
                                    builder.create().show();
                                }
                            });
                        }
                        else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    ToastUtil.show(context, "注销失败，原密码输入错误");
                                }
                            });
                        }
                    }
                }).start();
            }
            // 验证码方式
            else if (rb_verifycode.isChecked()) {
                String verifyvode = et_old_password.getText().toString();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        PatientDao patientDao = new PatientDao();
                        Patient patient = patientDao.searchById(user_name);
                        String true_verifyCode = null;
                        if (verifyUtil.isEmail(user_name)) {
                            true_verifyCode = gt_verifyCode;
                        }
                        else if (verifyUtil.isPhoneNumber(user_name)) {
                            true_verifyCode = preset_verifyCode;
                        }
                        // 检验验证码是否正确
                        if (patient != null && verifyvode.equals(true_verifyCode)) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(patient_del_account.this);
                            builder.setTitle("确认注销");
                            builder.setMessage("尊敬的用户 " + user_name + "\n您确定要注销账户吗?");
                            builder.setPositiveButton("是", (dialog, which)->{
                                // 删除患者用户
                                long success = patientDao.delete(user_name);
                                if (success > 0) {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            ToastUtil.show(context, "注销成功");
                                            // 跳转到登录界面
                                            Intent intent = new Intent(context, patient_login.class);
                                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                            startActivity(intent);
                                        }
                                    });
                                } else {
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            ToastUtil.show(context, "注销失败");
                                        }
                                    });
                                }
                            });
                            builder.setNegativeButton("否", null);
                            builder.create().show();
                        }
                        else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    ToastUtil.show(context, "注销失败，验证码输入错误");
                                }
                            });
                        }
                    }
                }).start();
            }
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