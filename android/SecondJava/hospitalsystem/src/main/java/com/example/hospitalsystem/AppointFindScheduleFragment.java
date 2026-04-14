package com.example.hospitalsystem;

import android.app.DatePickerDialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.hospitalsystem.adapter.AppointmentScheduleBaseAdapter;
import com.example.hospitalsystem.database.HospitalSysDatabase;
import com.example.hospitalsystem.db_Dao.ScheduleDao;
import com.example.hospitalsystem.db_Entity.Payment;
import com.example.hospitalsystem.db_Entity.Schedule;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AppointFindScheduleFragment extends Fragment implements AdapterView.OnItemClickListener, TextView.OnEditorActionListener, View.OnClickListener {

    private String user_name;
    private String office_name;
    private HospitalSysDatabase hospitalSysDB;
    private TextView tv_nomatch;
    private EditText et_doctor;
    private TextView et_date;
    private ImageView iv_doctor_search;
    private ListView lv_schedule;
    private AppointmentScheduleBaseAdapter adapter;
    private int office_id;
    private ImageView iv_clear_text;

    public AppointFindScheduleFragment() {
        // Required empty public constructor
    }

    public static AppointFindScheduleFragment newInstance(String user_name, String office_name, int office_id) {
        AppointFindScheduleFragment fragment = new AppointFindScheduleFragment();
        Bundle args = new Bundle();
        args.putString("user_name", user_name);
        args.putString("office_name", office_name);
        args.putInt("office_id", office_id);
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
        View view = inflater.inflate(R.layout.fragment_appoint_find_schedule, container, false);
        // 获取从科室选择fragment中传入的参数
        user_name = getArguments().getString("user_name");
        office_name = getArguments().getString("office_name");
        office_id = getArguments().getInt("office_id");

        // 数据库
        hospitalSysDB = MyApplication.getInstance().getHospitalDB();
        // 获取布局控件
        et_date = view.findViewById(R.id.et_date);
        et_doctor = view.findViewById(R.id.et_doctor);
        iv_doctor_search = view.findViewById(R.id.iv_doctor_search);
        lv_schedule = view.findViewById(R.id.lv_schedule);
        iv_clear_text = view.findViewById(R.id.iv_clear_text);

        // 为日期编辑器设置监听
        et_date.setOnClickListener(this);
        // 为医生姓名搜索栏设置监听
        et_doctor.setOnEditorActionListener(this);
        // 为搜索图标设置监听
        iv_doctor_search.setOnClickListener(this);
        // 为清除文本图标设置点击事件监听
        iv_clear_text.setOnClickListener(this);

        // 设置标题为科室名称
        TextView tv_title = view.findViewById(R.id.tv_title);
        tv_title.setText(office_name);
        // 设置返回事件
        ImageView iv_back = view.findViewById(R.id.iv_back);
        iv_back.setOnClickListener(v -> {
            // 回退到前一个fragment
            getActivity().getSupportFragmentManager().popBackStack();
        });
        tv_nomatch = view.findViewById(R.id.tv_nomatch);
        // 列表视图
        ListView lv_schedule = view.findViewById(R.id.lv_schedule);

        // 启动子线程，根据office_id查询所有的排班信息
        new Thread(new Runnable() {
            @Override
            public void run() {
                ScheduleDao scheduleDao = new ScheduleDao();
                List<Schedule> scheduleList = scheduleDao.searchByOfficeId(office_id);
                getActivity().runOnUiThread(()->{
                    if (scheduleList != null && !scheduleList.isEmpty()) {
                        adapter = new AppointmentScheduleBaseAdapter(scheduleList, user_name, office_name, office_id, getActivity());
                        lv_schedule.setAdapter(adapter);
                        tv_nomatch.setVisibility(View.GONE);
                    }
                    else {
                        tv_nomatch.setVisibility(View.VISIBLE);
                    }
                });

            }
        }).start();

        lv_schedule.setOnItemClickListener(this);
        return view;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

    }

    // 设置回车键盘事件监听
    @Override
    public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
        if (i == EditorInfo.IME_ACTION_SEARCH) {
            // 获取日期
            String dateStr = et_date.getText().toString();
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy年MM月dd日");
            SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");
            String date_output = "";
            try {
                Date date = inputFormat.parse(dateStr);
                // 将 Date 对象格式化为目标格式字符串
                date_output = outputFormat.format(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            // 获取医生姓名
            String name = et_doctor.getText().toString();
            String finalDateStr = date_output;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    ScheduleDao scheduleDao = new ScheduleDao();
                    List<Schedule> scheduleList = scheduleDao.searchByOfficeIdAndDoctorName(office_id, finalDateStr, name);
                    getActivity().runOnUiThread(()->{
                        if (scheduleList != null && !scheduleList.isEmpty()) {
                            tv_nomatch.setVisibility(View.GONE);
                            lv_schedule.setVisibility(View.VISIBLE);
                            adapter.updateOfficeList(scheduleList);
                        } else {
                            tv_nomatch.setVisibility(View.VISIBLE);
                            lv_schedule.setVisibility(View.GONE);
                            adapter.updateOfficeList(scheduleList);
                        }
                    });
                }
            }).start();
            return true;
        }
        return false;
    }

    // 日期展示
    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog.OnDateSetListener datePickerListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int selectedYear, int selectedMonth, int selectedDay) {
                // 使用SimpleDateFormat来格式化日期
                SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy年MM月dd日", Locale.getDefault());
                Date date = new Date(selectedYear - 1900, selectedMonth, selectedDay);
                String selectedDate = dateFormatter.format(date);
                et_date.setText(selectedDate);
            }
        };

        DatePickerDialog dialog = new DatePickerDialog(
                getActivity(),
                datePickerListener,
                year,
                month,
                day
        );
        dialog.show();
    }

    @Override
    public void onClick(View v) {
        // 点击医生搜索框
        if (v.getId() == R.id.iv_doctor_search) {
            // 获取日期
            String dateStr = et_date.getText().toString();
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy年MM月dd日");
            SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");
            String date_output = "";
            try {
                Date date = inputFormat.parse(dateStr);
                // 将 Date 对象格式化为目标格式字符串
                date_output = outputFormat.format(date);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            // 获取医生姓名
            String name = et_doctor.getText().toString();
            String finalDateStr = date_output;
            new Thread(new Runnable() {
                @Override
                public void run() {
                    ScheduleDao scheduleDao = new ScheduleDao();
                    List<Schedule> scheduleList = scheduleDao.searchByOfficeIdAndDoctorName(office_id, finalDateStr, name);
                    getActivity().runOnUiThread(()->{
                        if (scheduleList != null && !scheduleList.isEmpty()) {
                            tv_nomatch.setVisibility(View.GONE);
                            lv_schedule.setVisibility(View.VISIBLE);
                            adapter.updateOfficeList(scheduleList);
                        } else {
                            tv_nomatch.setVisibility(View.VISIBLE);
                            lv_schedule.setVisibility(View.GONE);
                            adapter.updateOfficeList(scheduleList);
                        }
                    });
                }
            }).start();
        }
        // 点击日期设置框
        else if (v.getId() == R.id.et_date) {
            showDatePicker();
        }
        // 点击清除编辑框文本图标
        else if (v.getId() == R.id.iv_clear_text) {
            et_date.setText("");
        }
    }
}