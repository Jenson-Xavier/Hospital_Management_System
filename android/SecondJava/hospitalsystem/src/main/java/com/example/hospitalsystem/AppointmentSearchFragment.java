package com.example.hospitalsystem;

import android.app.DatePickerDialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.hospitalsystem.adapter.DelAppointmentBaseAdapter;
import com.example.hospitalsystem.adapter.SearchAppointmentBaseAdapter;
import com.example.hospitalsystem.database.HospitalSysDatabase;
import com.example.hospitalsystem.db_Dao.AppointmentDao;
import com.example.hospitalsystem.db_Dao.OfficeDao;
import com.example.hospitalsystem.db_Entity.Appointment;
import com.example.hospitalsystem.db_Entity.Office;
import com.example.hospitalsystem.util.ToastUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;


public class AppointmentSearchFragment extends Fragment implements View.OnClickListener, TextView.OnEditorActionListener {


    private String user_name;
    private HospitalSysDatabase hospitalSysDB;
    private TextView et_date;
    private Spinner sp_office;
    private EditText et_doctor;
    private ImageView iv_clear_text;
    private ImageView iv_doctor_search;
    private TextView tv_nomatch;
    private ListView lv_appointment;
    private SearchAppointmentBaseAdapter adapter;
    private List<String> officeNameList;

    public AppointmentSearchFragment() {
        // Required empty public constructor
    }


    public static AppointmentSearchFragment newInstance(String user_name) {
        AppointmentSearchFragment fragment = new AppointmentSearchFragment();
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
        View view = inflater.inflate(R.layout.fragment_appointment_search, container, false);

        // 获取参数
        user_name = getArguments().getString("user_name");
        // 数据库
        hospitalSysDB = MyApplication.getInstance().getHospitalDB();
        // 设置标题
        TextView tv_title = view.findViewById(R.id.tv_title);
        tv_title.setText("我的预约");
        // 设置返回按钮监听
        ImageView iv_back = view.findViewById(R.id.iv_back);
        iv_back.setOnClickListener(v -> {
            // 回退到前一个fragment
            getActivity().getSupportFragmentManager().popBackStack();
        });

        // 获取布局控件
        et_date = view.findViewById(R.id.et_date);
        sp_office = view.findViewById(R.id.sp_office);
        et_doctor = view.findViewById(R.id.et_doctor);
        iv_clear_text = view.findViewById(R.id.iv_clear_text);
        iv_doctor_search = view.findViewById(R.id.iv_doctor_search);
        tv_nomatch = view.findViewById(R.id.tv_nomatch);
        lv_appointment = view.findViewById(R.id.lv_appointment);

        // 选择日期
        et_date.setOnClickListener(this);
        iv_clear_text.setOnClickListener(this);

        new Thread(new Runnable() {
            @Override
            public void run() {
                // 查询全部科室
                OfficeDao officeDao = new OfficeDao();
                List<Office> officeList = officeDao.searchAll();
                // 转化为科室名称列表
                List<String> officeNameList = officeList.stream()
                        .map(com.example.hospitalsystem.db_Entity.Office::getName)
                        .collect(Collectors.toList());
                officeNameList.add(0, "不限");
                String[] officeNames = officeNameList.toArray(new String[0]);

                // 查询当前患者的全部预约记录
                AppointmentDao appointmentDao = new AppointmentDao();
                List<Appointment> appointmentList = appointmentDao.searchByPatientId(user_name);

                // 返回主线程
                getActivity().runOnUiThread(()->{
                    // 设置科室下拉列表适配器
                    ArrayAdapter<String> startAdapter = new ArrayAdapter<>(getActivity(), R.layout.office_item_select, officeNames);
                    sp_office.setPrompt("请选择科室");
                    sp_office.setAdapter(startAdapter);
                    sp_office.setSelection(0);

                    if (appointmentList != null && !appointmentList.isEmpty()) {
                        adapter = new SearchAppointmentBaseAdapter(user_name, getActivity(), appointmentList);
                        lv_appointment.setAdapter(adapter);
                        tv_nomatch.setVisibility(View.GONE);
                        lv_appointment.setVisibility(View.VISIBLE);
                    } else {
                        tv_nomatch.setVisibility(View.VISIBLE);
                        lv_appointment.setVisibility(View.GONE);
                    }
                });

            }
        }).start();

        // 搜索医生姓名
        et_doctor.setOnEditorActionListener(this);
        iv_doctor_search.setOnClickListener(this);

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
        // 日期选择
        if (v.getId() == R.id.et_date) {
            showDatePicker();
        }
        // 清除文本
        else if (v.getId() == R.id.iv_clear_text) {
            et_date.setText("");
        }
        // 搜索医生
        else if (v.getId() == R.id.iv_doctor_search) {
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
            String finalDateStr = date_output;
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

                    // 根据科室id、医生姓名、患者用户名、日期查询预约记录
                    AppointmentDao appointmentDao = new AppointmentDao();
                    List<Appointment> appointmentList = appointmentDao.searchByDoctorNameAndPatientIdAndOfficeId(doctor_name, user_name, office_id, finalDateStr);

                    // 返回主线程
                    getActivity().runOnUiThread(()->{
                        if (appointmentList != null && !appointmentList.isEmpty()) {
                            tv_nomatch.setVisibility(View.GONE);
                            lv_appointment.setVisibility(View.VISIBLE);
                        } else {
                            tv_nomatch.setVisibility(View.VISIBLE);
                            lv_appointment.setVisibility(View.GONE);
                        }
                        adapter.updateAppointmentList(appointmentList);
                    });
                }
            }).start();
        }
    }

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
            String finalDateStr = date_output;
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

                    // 根据科室id、医生姓名、患者用户名、日期查询预约记录
                    AppointmentDao appointmentDao = new AppointmentDao();
                    List<Appointment> appointmentList = appointmentDao.searchByDoctorNameAndPatientIdAndOfficeId(doctor_name, user_name, office_id, finalDateStr);

                    // 返回主线程
                    getActivity().runOnUiThread(()->{
                        if (appointmentList != null && !appointmentList.isEmpty()) {
                            tv_nomatch.setVisibility(View.GONE);
                            lv_appointment.setVisibility(View.VISIBLE);
                        } else {
                            tv_nomatch.setVisibility(View.VISIBLE);
                            lv_appointment.setVisibility(View.GONE);
                        }
                        adapter.updateAppointmentList(appointmentList);
                    });
                }
            }).start();
            return true;
        }
        return false;
    }

}