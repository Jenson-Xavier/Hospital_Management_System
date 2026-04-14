package com.example.hospitalsystem;

import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.hospitalsystem.adapter.DoctorInfoBaseAdapter;
import com.example.hospitalsystem.database.HospitalSysDatabase;
import com.example.hospitalsystem.db_Dao.DoctorInfoDao;
import com.example.hospitalsystem.db_Dao.OfficeDao;
import com.example.hospitalsystem.db_Entity.DoctorInfo;
import com.example.hospitalsystem.db_Entity.Office;

import java.util.List;
import java.util.stream.Collectors;


public class DoctorFindFragment extends Fragment implements TextView.OnEditorActionListener, View.OnClickListener {


    private String user_name;
    private HospitalSysDatabase hospitalSysDB;
    private Spinner sp_office;
    private EditText et_doctor;
    private ImageView iv_doctor_search;
    private TextView tv_nomatch;
    private List<String> officeNameList;
    private ListView lv_doctorInfo;
    private DoctorInfoBaseAdapter adapter;
    private String type;
    private Context context;

    public DoctorFindFragment() {
        // Required empty public constructor
    }


    // type表示该页面的作用, "appoint"和"comment"
    public static DoctorFindFragment newInstance(String user_name, String type) {
        DoctorFindFragment fragment = new DoctorFindFragment();
        Bundle args = new Bundle();
        args.putString("user_name", user_name);
        args.putString("type", type);
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
        View view = inflater.inflate(R.layout.fragment_doctor_find, container, false);

        // 获取参数
        user_name = getArguments().getString("user_name");
        type = getArguments().getString("type");
        hospitalSysDB = MyApplication.getInstance().getHospitalDB();

        // 获取布局控件
        TextView tv_title = view.findViewById(R.id.tv_title);
        ImageView iv_back = view.findViewById(R.id.iv_back);
        sp_office = view.findViewById(R.id.sp_office);
        et_doctor = view.findViewById(R.id.et_doctor);
        iv_doctor_search = view.findViewById(R.id.iv_doctor_search);
        tv_nomatch = view.findViewById(R.id.tv_nomatch);
        lv_doctorInfo = view.findViewById(R.id.lv_doctorInfo);

        // 设置标题
        tv_title.setText("查询医生");
        iv_back.setOnClickListener(v -> {
            getActivity().getSupportFragmentManager().popBackStack();
        });

        new Thread(new Runnable() {
            @Override
            public void run() {
                // 查询全部科室信息
                OfficeDao officeDao = new OfficeDao();
                List<Office> officeList = officeDao.searchAll();
                // 转化为科室名称列表
                List<String> officeNameList = officeList.stream()
                        .map(Office::getName)
                        .collect(Collectors.toList());
                officeNameList.add(0, "不限");
                String[] officeNames = officeNameList.toArray(new String[0]);

                // 查询所有医生信息
                DoctorInfoDao doctorInfoDao = new DoctorInfoDao();
                List<DoctorInfo> doctorInfoList = doctorInfoDao.searchAll();

                // 返回主线程
                getActivity().runOnUiThread(()->{
                    // 科室选择下拉列表
                    ArrayAdapter<String> startAdapter = new ArrayAdapter<>(getActivity(), R.layout.office_item_select, officeNames);
                    sp_office.setPrompt("请选择科室");
                    sp_office.setAdapter(startAdapter);
                    sp_office.setSelection(0);

                    // 医生信息ListView适配器
                    adapter = new DoctorInfoBaseAdapter(user_name, getActivity(), doctorInfoList, type);
                    lv_doctorInfo.setAdapter(adapter);
                    // 如果不存在排班信息，则显示提示信息
                    if (doctorInfoList != null && !doctorInfoList.isEmpty()){
                        lv_doctorInfo.setVisibility(View.VISIBLE);
                        tv_nomatch.setVisibility(View.GONE);
                    }
                    else {
                        lv_doctorInfo.setVisibility(View.GONE);
                        tv_nomatch.setVisibility(View.VISIBLE);
                    }
                });
            }
        }).start();

        // 搜索医生
        et_doctor.setOnEditorActionListener(this);
        iv_doctor_search.setOnClickListener(this);

        return view;
    }

    @Override
    public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
        if (i == EditorInfo.IME_ACTION_SEARCH) {
            // 获取医生姓名
            String doctor_name = et_doctor.getText().toString();
            // 获取科室名称+id
            String office_name = sp_office.getSelectedItem().toString();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    OfficeDao officeDao = new OfficeDao();
                    List<Office> officeList = officeDao.searchByName(office_name);
                    int office_id = office_name.equals("不限") ? -1 : officeList.get(0).getOfficeId();

                    // 根据科室id和医生姓名模糊查询医生信息
                    DoctorInfoDao doctorInfoDao = new DoctorInfoDao();
                    List<DoctorInfo> doctorInfoList = doctorInfoDao.searchByOfficeIdAndDoctorName(office_id, doctor_name);

                    // 返回主线程
                    getActivity().runOnUiThread(()->{
                        if (doctorInfoList != null && !doctorInfoList.isEmpty()) {
                            tv_nomatch.setVisibility(View.GONE);
                            lv_doctorInfo.setVisibility(View.VISIBLE);
                        }
                        else {
                            tv_nomatch.setVisibility(View.VISIBLE);
                            lv_doctorInfo.setVisibility(View.GONE);
                        }
                        adapter.updateOfficeList(doctorInfoList);
                    });
                }
            }).start();
            return true;
        }
        return false;
    }

    @Override
    public void onClick(View v) {
        // 搜索医生
        if (v.getId() == R.id.iv_doctor_search) {
            // 获取医生姓名
            String doctor_name = et_doctor.getText().toString();
            // 获取科室名称+id
            String office_name = sp_office.getSelectedItem().toString();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    OfficeDao officeDao = new OfficeDao();
                    List<Office> officeList = officeDao.searchByName(office_name);
                    int office_id = office_name.equals("不限") ? -1 : officeList.get(0).getOfficeId();

                    // 根据科室id和医生姓名模糊查询医生信息
                    DoctorInfoDao doctorInfoDao = new DoctorInfoDao();
                    List<DoctorInfo> doctorInfoList = doctorInfoDao.searchByOfficeIdAndDoctorName(office_id, doctor_name);

                    // 返回主线程
                    getActivity().runOnUiThread(()->{
                        if (doctorInfoList != null && !doctorInfoList.isEmpty()) {
                            tv_nomatch.setVisibility(View.GONE);
                            lv_doctorInfo.setVisibility(View.VISIBLE);
                        }
                        else {
                            tv_nomatch.setVisibility(View.VISIBLE);
                            lv_doctorInfo.setVisibility(View.GONE);
                        }
                        adapter.updateOfficeList(doctorInfoList);
                    });
                }
            }).start();
        }
    }
}