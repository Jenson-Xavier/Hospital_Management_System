package com.example.hospitalsystem;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class patient_index extends AppCompatActivity implements NavigationBarView.OnItemSelectedListener {

    // 用户名
    private String user_name;
    public static Fragment currentFragment = null;
    public static Fragment indexFragment = null;
    public static Fragment appointmentManageFragment = null;
    public static Fragment paymentFindFragment = null;
    public static Fragment userManageFragment = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_patient_index);
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        // 从用户登录界面/寻找科室页面获取返回值
        Intent intent = getIntent();
        user_name = intent.getStringExtra("user_name");

        // 默认显示首页
        if (savedInstanceState == null){
            if (indexFragment == null) {
                indexFragment = IndexFragment.newInstance(user_name);
            }
            currentFragment = indexFragment;
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, indexFragment).commit();
        }

        // 为底部导航栏设置选择监听
        bottomNavigationView.setOnItemSelectedListener(this);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.nv_index) {
            if (indexFragment == null) {
                indexFragment = IndexFragment.newInstance(user_name);
            }
            switchContent(currentFragment, indexFragment);
            return true;
        }
        else if (item.getItemId() == R.id.nv_appointment) {
            if (appointmentManageFragment == null) {
                appointmentManageFragment = AppointmentManageFragment.newInstance(user_name);
            }
            switchContent(currentFragment, appointmentManageFragment);
            return true;
        }
        else if (item.getItemId() == R.id.nv_payment) {
            if (paymentFindFragment == null) {
                paymentFindFragment = PaymentFindFragment.newInstance(user_name);
            }
            switchContent(currentFragment, paymentFindFragment);
            return true;
        }
//        else if (item.getItemId() == R.id.nv_refund) {
//
//        }
        else if (item.getItemId() == R.id.nv_user) {
            if (userManageFragment == null) {
                userManageFragment = UserManageFragment.newInstance(user_name);
            }
            switchContent(currentFragment, userManageFragment);
            return true;
        }
        return false;
    }

    public void switchContent(Fragment from, Fragment to) {
        if (from == null || to == null) return;

        if (currentFragment != to) {
            currentFragment = to;
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, to)
                    .addToBackStack(null)
                    .commit();
        }
    }
}