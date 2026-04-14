package com.example.hospitalsystem;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.example.hospitalsystem.util.ToastUtil;


public class AppointmentManageFragment extends Fragment implements View.OnClickListener {


    private RelativeLayout rl_appointment;
    private RelativeLayout rl_delete_appointment;
    private RelativeLayout rl_appointment_records;
    private String user_name;

    public AppointmentManageFragment() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static AppointmentManageFragment newInstance(String user_name) {
        AppointmentManageFragment fragment = new AppointmentManageFragment();
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
        View view = inflater.inflate(R.layout.fragment_appointment_manage, container, false);
        user_name = getArguments().getString("user_name");
        // 获取布局控件
        rl_appointment = view.findViewById(R.id.rl_appointment);
        rl_delete_appointment = view.findViewById(R.id.rl_delete_appointment);
        rl_appointment_records = view.findViewById(R.id.rl_appointment_records);
        // 设置点击事件监听
        rl_appointment.setOnClickListener(this);
        rl_delete_appointment.setOnClickListener(this);
        rl_appointment_records.setOnClickListener(this);
        return view;
    }

    // 点击事件监听
    @Override
    public void onClick(View v) {
        // 预约申请
        if (v.getId() == R.id.rl_appointment) {
            // 在此传递参数
            Fragment fragment = AppointFindOfficeFragment.newInstance(user_name);
            // 切换活动中的fragment
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .addToBackStack(null)
                    .commit();
            ToastUtil.show(getActivity(), "您已进入预约申请界面，请首先选择科室");
        }
        // 取消预约
        else if (v.getId() == R.id.rl_delete_appointment) {
            Fragment fragment = AppointmentCancelFragment.newInstance(user_name);
            // 切换活动中的fragment
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .addToBackStack(null)
                    .commit();
            ToastUtil.show(getActivity(), "您已进入取消预约界面");
        }
        // 查看预约记录
        else if (v.getId() == R.id.rl_appointment_records) {
            Fragment fragment = AppointmentSearchFragment.newInstance(user_name);
            // 切换活动中的fragment
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .addToBackStack(null)
                    .commit();
            ToastUtil.show(getActivity(), "您已进入查看预约记录界面");
        }
    }
}