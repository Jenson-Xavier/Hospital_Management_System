package com.example.secondjava;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.example.secondjava.Util.ToastUtil;
import com.example.secondjava.dao.UserDao;
import com.example.secondjava.enity.User_room;

public class RoomWriteActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText et_name;
    private EditText et_age;
    private EditText et_height;
    private EditText et_weight;
    private CheckBox ck_married;
    private UserDao userDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_room_write);

        et_name = findViewById(R.id.et_name);
        et_age = findViewById(R.id.et_age);
        et_height = findViewById(R.id.et_height);
        et_weight = findViewById(R.id.et_weight);
        ck_married = findViewById(R.id.ck_married);

        findViewById(R.id.btn_add).setOnClickListener(this);
        findViewById(R.id.btn_delete).setOnClickListener(this);
        findViewById(R.id.btn_update).setOnClickListener(this);
        findViewById(R.id.btn_search).setOnClickListener(this);

        // 从App实例中获取唯一的用户持久化对象
        userDao = MyApplication.getInstance().getUserDB().userDao();
    }

    @Override
    public void onClick(View v) {
        String name = et_name.getText().toString();
        String age = et_age.getText().toString();
        String height = et_height.getText().toString();
        String weight = et_weight.getText().toString();
        User_room user = null;
        if (v.getId() == R.id.btn_add) {
            user = new User_room();
            user.setName(name);
            user.setAge(Integer.parseInt(age));
            user.setHeight(Long.parseLong(height));
            user.setWeight(Float.parseFloat(weight));
            user.setMarried(ck_married.isChecked());
            userDao.insert(user);
            ToastUtil.show(this, "增加成功");
        }
        else if (v.getId() == R.id.btn_delete) {
            // 先根据姓名查找用户id
            user = userDao.queryByName(name);
            // 再根据Id删除用户
            userDao.delete(user);
            ToastUtil.show(this, "删除成功");
        }
        else if (v.getId() == R.id.btn_update) {
            // 先根据姓名查找用户id
            user = userDao.queryByName(name);
            user.setName(name);
            user.setAge(Integer.parseInt(age));
            user.setHeight(Long.parseLong(height));
            user.setWeight(Float.parseFloat(weight));
            user.setMarried(ck_married.isChecked());
            userDao.update(user);
            ToastUtil.show(this, "修改成功");
        }
        else if (v.getId() == R.id.btn_search) {
            user = userDao.queryByName(name);
            Log.d("tjy", user.toString());
        }
    }
}