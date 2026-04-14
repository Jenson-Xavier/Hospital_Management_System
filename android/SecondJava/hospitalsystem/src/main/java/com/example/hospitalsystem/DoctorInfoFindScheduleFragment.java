package com.example.hospitalsystem;

import android.app.DatePickerDialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.hospitalsystem.adapter.DoctorInfoScheduleBaseAdapter;
import com.example.hospitalsystem.database.HospitalSysDatabase;
import com.example.hospitalsystem.db_Dao.ScheduleDao;
import com.example.hospitalsystem.db_Entity.Payment;
import com.example.hospitalsystem.db_Entity.Schedule;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class DoctorInfoFindScheduleFragment extends Fragment implements View.OnClickListener {


    private String user_name;
    private String doctor_name;
    private String doctor_id;
    private TextView tv_date;
    private ImageView iv_clear_text;
    private Spinner sp_period;
    private ImageView iv_search;
    private TextView tv_nomatch;
    private HospitalSysDatabase hospitalSysDB;
    private String[] periodList;
    private ListView lv_schedule;
    private DoctorInfoScheduleBaseAdapter adapter;

    public DoctorInfoFindScheduleFragment() {
        // Required empty public constructor
    }


    public static DoctorInfoFindScheduleFragment newInstance(String user_name, String doctor_id, String doctor_name) {
        DoctorInfoFindScheduleFragment fragment = new DoctorInfoFindScheduleFragment();
        Bundle args = new Bundle();
        args.putString("user_name", user_name);
        args.putString("doctor_id", doctor_id);
        args.putString("doctor_name", doctor_name);
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
        View view = inflater.inflate(R.layout.fragment_doctor_info_find_schedule, container, false);

        // 获取参数
        user_name = getArguments().getString("user_name");
        doctor_name = getArguments().getString("doctor_name");
        doctor_id = getArguments().getString("doctor_id");
        hospitalSysDB = MyApplication.getInstance().getHospitalDB();

        // 获取布局控件
        TextView tv_title = view.findViewById(R.id.tv_title);
        ImageView iv_back = view.findViewById(R.id.iv_back);
        tv_date = view.findViewById(R.id.tv_date);
        iv_clear_text = view.findViewById(R.id.iv_clear_text);
        sp_period = view.findViewById(R.id.sp_period);
        iv_search = view.findViewById(R.id.iv_search);
        tv_nomatch = view.findViewById(R.id.tv_nomatch);
        lv_schedule = view.findViewById(R.id.lv_schedule);

        // 设置标题
        tv_title.setText(doctor_name);
        iv_back.setOnClickListener(v -> {
            getActivity().getSupportFragmentManager().popBackStack();
        });

        // 选择日期
        tv_date.setOnClickListener(this);
        iv_clear_text.setOnClickListener(this);

        // 选择时段
        periodList = Schedule.periodStr;
        ArrayAdapter<String> startAdapter = new ArrayAdapter<>(getActivity(), R.layout.period_item_select, periodList);
        sp_period.setPrompt("请选择就诊时段");
        sp_period.setAdapter(startAdapter);
        sp_period.setSelection(0);

        // 搜索图标
        iv_search.setOnClickListener(this);

        // 设置适配器
        new Thread(new Runnable() {
            @Override
            public void run() {
                ScheduleDao scheduleDao = new ScheduleDao();
                List<Schedule> scheduleList = scheduleDao.searchByDoctorId(doctor_id);

                getActivity().runOnUiThread(()->{
                    if (scheduleList != null && !scheduleList.isEmpty()) {
                        adapter = new DoctorInfoScheduleBaseAdapter(user_name, doctor_id, getActivity(), doctor_name, scheduleList);
                        lv_schedule.setAdapter(adapter);
                        lv_schedule.setVisibility(View.VISIBLE);
                        tv_nomatch.setVisibility(View.GONE);
                    } else {
                        lv_schedule.setVisibility(View.GONE);
                        tv_nomatch.setVisibility(View.VISIBLE);
                    }

                });
            }
        }).start();
        return view;
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
                tv_date.setText(selectedDate);
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
        // 清除文本
        if (v.getId() == R.id.iv_clear_text) {
            tv_date.setText("");
        }
        // 设设置日期
        else if (v.getId() == R.id.tv_date) {
            showDatePicker();
        }
        // 搜索排班信息
        else if (v.getId() == R.id.iv_search) {
            // 获取日期
            String dateStr = tv_date.getText().toString();
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
            String finalDateStr = date_output;

            // 获取就诊时段
            String periodStr = sp_period.getSelectedItem().toString();
            int PeriodIndex = Arrays.asList(periodList).indexOf(periodStr);       // 获取所选时段对应的下标

            new Thread(new Runnable() {
                @Override
                public void run() {
                    ScheduleDao scheduleDao = new ScheduleDao();
                    List<Schedule> scheduleList = scheduleDao.searchByDoctorIdAndDateAndPeriod(doctor_id, finalDateStr, PeriodIndex);
                    getActivity().runOnUiThread(()->{
                        if (scheduleList != null && !scheduleList.isEmpty()) {
                            tv_nomatch.setVisibility(View.GONE);
                            lv_schedule.setVisibility(View.VISIBLE);
                        } else {
                            tv_nomatch.setVisibility(View.VISIBLE);
                            lv_schedule.setVisibility(View.GONE);
                        }
                        adapter.updateAppointmentList(scheduleList);

                    });
                }
            }).start();
        }
    }

}