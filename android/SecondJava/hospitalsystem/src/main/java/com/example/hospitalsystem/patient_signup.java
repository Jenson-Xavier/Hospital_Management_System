package com.example.hospitalsystem;

import android.content.Context;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
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
import com.example.hospitalsystem.util.ViewUtil;
import com.example.hospitalsystem.util.verifyUtil;

public class patient_signup extends AppCompatActivity implements RadioGroup.OnCheckedChangeListener, View.OnClickListener, View.OnFocusChangeListener {

    private Button btn_sendverifycode;
    private Button btn_signup;
    private RadioButton rb_phone;
    private RadioButton rb_email;
    private RadioGroup rg_signup_way;
    private TextView tv_account;
    private TextView tv_login;
    private EditText et_account;
    private EditText et_verifycode;
    private EditText et_password;
    private EditText et_confirm;

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
        setContentView(R.layout.activity_patient_signup);

        et_account = findViewById(R.id.et_account);
        et_verifycode = findViewById(R.id.et_verifycode);
        et_password = findViewById(R.id.et_password);
        et_confirm = findViewById(R.id.et_confirm);
        tv_account = findViewById(R.id.tv_account);
        tv_login = findViewById(R.id.tv_login);
        rb_phone = findViewById(R.id.rb_phone);
        rb_email = findViewById(R.id.rb_email);
        btn_sendverifycode = findViewById(R.id.btn_sendverifycode);
        btn_signup = findViewById(R.id.btn_signup);
        rg_signup_way = findViewById(R.id.rg_signup_way);

        // rg
        rg_signup_way.setOnCheckedChangeListener(this);
        // btn
        btn_sendverifycode.setOnClickListener(this);
        btn_signup.setOnClickListener(this);
        // tv
        tv_login.setOnClickListener(this);
        // et
        et_account.addTextChangedListener(new HideTextWatcher(et_account, 11, true));
        et_verifycode.addTextChangedListener(new HideTextWatcher(et_verifycode, 6, false));
        et_confirm.setOnFocusChangeListener(this);

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

    // Rg_signup_way
    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int i) {
        if (i == R.id.rb_phone) {
            et_account.setHint("请输入手机号");
            et_account.setInputType(InputType.TYPE_CLASS_NUMBER);
            // 清空
            et_account.setText("");
            et_verifycode.setText("");
            et_password.setText("");
            et_confirm.setText("");
        }
        else if (i == R.id.rb_email) {
            et_account.setHint("请输入邮箱号");
            et_account.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
            // 清空
            et_account.setText("");
            et_verifycode.setText("");
            et_password.setText("");
            et_confirm.setText("");
        }
    }

    // btn
    @Override
    public void onClick(View v) {
        // 注册
        if (v.getId() == R.id.btn_signup) {
            String user_name = et_account.getText().toString();
            String verifycode = et_verifycode.getText().toString();
            String password = et_password.getText().toString();
            String password_again = et_confirm.getText().toString();
            String true_verifyCode = null;
            if (rb_email.isChecked()) {
                true_verifyCode = gt_verifyCode;
            }
            else if (rb_phone.isChecked()) {
                true_verifyCode = preset_verifyCode;
            }
            // 检验验证码
            if (!verifycode.equals(true_verifyCode)) {
                ToastUtil.show(this, "注册失败，验证码不正确");
                return;
            }
            // 检验密码输入是否一致
            if (!password_again.equals(password)) {
                ToastUtil.show(this, "注册失败，两次密码输入不一致");
                return;
            }
            // 检验密码强弱性
            if (!checkPwd(password)) {
                ToastUtil.show(this, "注册失败，密码未通过强弱性检验");
                return;
            }
            PatientDao patientDao = new PatientDao();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Patient patient = patientDao.searchById(user_name);
                    if (patient != null) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ToastUtil.show(context, "该用户名已经注册过，无法重复注册");
                            }
                        });
                        return;
                    }
                    patient = new Patient();
                    patient.setUserId(user_name);
                    patient.setPassword(password);
                    patient.setUserType(Patient.PATIENT);
                    long success = patientDao.insert(patient);
                    if (success > 0) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ToastUtil.show(context, "注册成功!");
                                finish();
                            }
                        });
                    }
                }
            }).start();
        }
        // 发送验证码
        else if (v.getId() == R.id.btn_sendverifycode) {
            String user_name = et_account.getText().toString();
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
        // 返回登录界面
        else if (v.getId() == R.id.tv_login) {
            finish();
        }
    }

    @Override
    public void onFocusChange(View v, boolean hasFocused) {
        if (v.getId() == R.id.et_confirm && hasFocused) {
            String password = et_password.getText().toString();
            String password_again = et_confirm.getText().toString();
            if (!password.equals(password_again)) {
                ToastUtil.show(this, "两次密码输入不一致");
            }
        }
    }

    // 定义一个编辑框监听器，在输入文本达到指定长度时自动隐藏输入法
    private class HideTextWatcher implements TextWatcher {

        private EditText mEt;
        private int mMaxlength;
        private boolean is_account;

        public HideTextWatcher(EditText et, int i, boolean is_account) {
            this.mEt = et;
            this.mMaxlength = i;
            this.is_account = is_account;
        }

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            if (s.toString().length() == mMaxlength) {
                // 如果是邮箱，则不受限
                if (is_account && rb_email.isChecked()) return;
                ViewUtil.hideOneInputMethod(patient_signup.this, mEt);
            }
        }
    }
}