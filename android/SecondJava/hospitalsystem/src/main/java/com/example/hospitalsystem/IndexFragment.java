package com.example.hospitalsystem;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.sax.RootElement;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.example.hospitalsystem.util.NavUtil;
import com.example.hospitalsystem.util.ToastUtil;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;


public class IndexFragment extends Fragment implements View.OnClickListener {


    private RelativeLayout rl_appointment;
    private RelativeLayout rl_doctor;
    private RelativeLayout rl_office;
    private RelativeLayout rl_comment;
    private String user_name;
    private Fragment appointFindOfficeFragment;
    private Fragment officeFindFragment;
    private Fragment doctorFindFragment;
    private Fragment commentManageFragment;

    public IndexFragment() {
        // Required empty public constructor
    }

    public static IndexFragment newInstance(String user_name) {
        IndexFragment fragment = new IndexFragment();
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
        View view =  inflater.inflate(R.layout.fragment_index, container, false);
        rl_appointment = view.findViewById(R.id.rl_appointment);
        rl_doctor = view.findViewById(R.id.rl_doctor);
        rl_office = view.findViewById(R.id.rl_office);
        rl_comment = view.findViewById(R.id.rl_comment);
        // 设置点击事件监听
        rl_appointment.setOnClickListener(this);
        rl_doctor.setOnClickListener(this);
        rl_office.setOnClickListener(this);
        rl_comment.setOnClickListener(this);
        // 获取用户名
        user_name = getArguments().getString("user_name");

        return view;
    }

    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
    }

    @Override
    public void onClick(View v) {
        // 预约挂号
        if (v.getId() == R.id.rl_appointment) {
            // 在此传递参数
            appointFindOfficeFragment = AppointFindOfficeFragment.newInstance(user_name);
            // 切换活动中的fragment
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, appointFindOfficeFragment)
                    .addToBackStack(null)
                    .commit();
            ToastUtil.show(getActivity(), "您已进入预约申请界面，请首先选择科室");
        }
        // 查询科室
        else if (v.getId() == R.id.rl_office) {
            // 在此传递参数
            officeFindFragment = OfficeFindFragment.newInstance(user_name);
            // 切换活动中的fragment
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, officeFindFragment)
                    .addToBackStack(null)
                    .commit();
            ToastUtil.show(getActivity(), "您已进入查询科室界面，请首先选择科室");
        }
        else if (v.getId() == R.id.rl_doctor) {
            // 在此传递参数
            doctorFindFragment = DoctorFindFragment.newInstance(user_name, "appoint");
            // 切换活动中的fragment
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, doctorFindFragment)
                    .addToBackStack(null)
                    .commit();
            ToastUtil.show(getActivity(), "您已进入查询医生界面，请首先选择医生");
        }
        else if (v.getId() == R.id.rl_comment) {
            // 在此传递参数
            commentManageFragment = CommentManageFragment.newInstance(user_name);
            // 切换活动中的fragment
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, commentManageFragment)
                    .addToBackStack(null)
                    .commit();
            ToastUtil.show(getActivity(), "您已进入评价社区");
        }
    }


}