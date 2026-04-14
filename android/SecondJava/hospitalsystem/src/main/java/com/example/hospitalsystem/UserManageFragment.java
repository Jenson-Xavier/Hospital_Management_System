package com.example.hospitalsystem;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.hospitalsystem.util.ToastUtil;


public class UserManageFragment extends Fragment implements View.OnClickListener {


    private LinearLayout ll_change_pwd;
    private LinearLayout ll_signout;
    private TextView tv_user_name;
    private LinearLayout ll_logout;
    private String user_name;

    public UserManageFragment() {
        // Required empty public constructor
    }

    public static UserManageFragment newInstance(String user_name) {
        UserManageFragment fragment = new UserManageFragment();
        Bundle args = new Bundle();
        args.putString("user_name", user_name);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_manage, container, false);
        Bundle arguments = getArguments();
        if (arguments != null) {
            // 设置用户名
            tv_user_name = view.findViewById(R.id.tv_user_name);
            user_name = arguments.getString("user_name");
            tv_user_name.setText(user_name);
        }
        // 设置点击事件监听
        ll_change_pwd = view.findViewById(R.id.ll_change_pwd);
        ll_signout = view.findViewById(R.id.ll_signout);
        ll_logout = view.findViewById(R.id.ll_logout);

        ll_change_pwd.setOnClickListener(this);
        ll_signout.setOnClickListener(this);
        ll_logout.setOnClickListener(this);
        return view;
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
    }

    @Override
    public void onClick(View v) {
        // 修改密码
        if (v.getId() == R.id.ll_change_pwd) {
            Intent intent = new Intent(getActivity(), patient_update_password.class);
            intent.putExtra("user_name", tv_user_name.getText().toString());
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
        // 注销账号
        else if (v.getId() == R.id.ll_signout) {
            Intent intent = new Intent(getActivity(), patient_del_account.class);
            intent.putExtra("user_name", tv_user_name.getText().toString());
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
        // 退出登录
        else if (v.getId() == R.id.ll_logout) {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("退出登录");
            builder.setMessage("尊敬的用户 " + user_name + "\n您确定要退出登录吗?");
            builder.setPositiveButton("是", (dialog, which)->{
                // 删除患者用户
                ToastUtil.show(getActivity(), "注销成功");
                // 跳转到登录界面
                Intent intent = new Intent(getActivity(), patient_login.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            });
            builder.setNegativeButton("否", null);
            builder.create().show();
        }
    }
}