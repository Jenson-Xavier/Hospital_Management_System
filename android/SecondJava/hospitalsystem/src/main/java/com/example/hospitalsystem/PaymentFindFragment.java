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

import com.example.hospitalsystem.adapter.CommentManageBaseAdapter;
import com.example.hospitalsystem.adapter.PaymentBaseAdapter;
import com.example.hospitalsystem.database.HospitalSysDatabase;
import com.example.hospitalsystem.db_Dao.CommentDao;
import com.example.hospitalsystem.db_Dao.OfficeDao;
import com.example.hospitalsystem.db_Dao.PaymentDao;
import com.example.hospitalsystem.db_Entity.Comment;
import com.example.hospitalsystem.db_Entity.Office;
import com.example.hospitalsystem.db_Entity.Payment;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

public class PaymentFindFragment extends Fragment implements View.OnClickListener {


    private String user_name;
    private HospitalSysDatabase hospitalSysDB;
    private ImageView iv_back;
    private TextView tv_title;
    private TextView tv_date;
    private TextView tv_nomatch;
    private ListView lv_payment;
    private ImageView iv_clear_text;
    private Spinner sp_office;
    private ImageView iv_search;
    private PaymentBaseAdapter adapter;

    public PaymentFindFragment() {
        // Required empty public constructor
    }

    public static PaymentFindFragment newInstance(String user_name) {
        PaymentFindFragment fragment = new PaymentFindFragment();
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
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_payment_find, container, false);

        // 获取参数
        user_name = getArguments().getString("user_name");
        hospitalSysDB = MyApplication.getInstance().getHospitalDB();

        // 获取布局控件
        iv_back = view.findViewById(R.id.iv_back);
        tv_title = view.findViewById(R.id.tv_title);
        tv_date = view.findViewById(R.id.et_date);
        sp_office = view.findViewById(R.id.sp_office);
        tv_nomatch = view.findViewById(R.id.tv_nomatch);
        lv_payment = view.findViewById(R.id.lv_payment);
        iv_clear_text = view.findViewById(R.id.iv_clear_text);
        iv_search = view.findViewById(R.id.iv_search);

        // 设置标题
        tv_title.setText("我的支付");
        iv_back.setVisibility(View.GONE);
        iv_back.setOnClickListener(v -> {
            // 回退到前一个fragment
            getActivity().getSupportFragmentManager().popBackStack();
        });
        iv_clear_text.setOnClickListener(this);
        iv_search.setOnClickListener(this);

        // 选择日期
        tv_date.setOnClickListener(this);

        // 选择科室
        new Thread(new Runnable() {
            @Override
            public void run() {
                // 查询全部科室
                OfficeDao officeDao = new OfficeDao();
                List<Office> officeList = officeDao.searchAll();
                // 转化为科室名称列表
                List<String> officeNameList = officeList.stream()
                        .map(Office::getName)
                        .collect(Collectors.toList());
                officeNameList.add(0, "不限");
                String[] officeNames = officeNameList.toArray(new String[0]);

                // 根据患者用户名查询所有支付记录
                PaymentDao paymentDao = new PaymentDao();
                List<Payment> paymentList = paymentDao.searchByPatientId(user_name);

                // 返回主线程
                getActivity().runOnUiThread(()->{
                    // 设置科室下拉列表适配器
                    ArrayAdapter<String> startAdapter = new ArrayAdapter<>(getActivity(), R.layout.office_item_select, officeNames);
                    sp_office.setPrompt("请选择科室");
                    sp_office.setAdapter(startAdapter);
                    sp_office.setSelection(0);

                    if (paymentList != null && !paymentList.isEmpty()) {
                        adapter = new PaymentBaseAdapter(user_name, getActivity(), paymentList);
                        lv_payment.setAdapter(adapter);
                        tv_nomatch.setVisibility(View.GONE);
                        lv_payment.setVisibility(View.VISIBLE);
                    } else {
                        tv_nomatch.setVisibility(View.VISIBLE);
                        lv_payment.setVisibility(View.GONE);
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
        // 日期选择
        if (v.getId() == R.id.et_date) {
            showDatePicker();
        }
        // 清除文本
        else if (v.getId() == R.id.iv_clear_text) {
            tv_date.setText("");
        }
        // 搜索支付记录
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

            // 获取科室名称+id
            // 获取科室名称+id
            String office_name = sp_office.getSelectedItem().toString();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    OfficeDao officeDao = new OfficeDao();
                    List<Office> officeList = officeDao.searchByName(office_name);
                    int office_id = office_name.equals("不限") ? -1 : officeList.get(0).getOfficeId();

                    // 根据患者用户名、日期和科室id进行查询
                    PaymentDao paymentDao = new PaymentDao();
                    List<Payment> paymentList = paymentDao.searchByPatientIdAndOfficeId(user_name, office_id, finalDateStr);

                    // 返回主线程
                    getActivity().runOnUiThread(()->{
                        if (paymentList != null && !paymentList.isEmpty()) {
                            tv_nomatch.setVisibility(View.GONE);
                            lv_payment.setVisibility(View.VISIBLE);
                        } else {
                            tv_nomatch.setVisibility(View.VISIBLE);
                            lv_payment.setVisibility(View.GONE);
                        }
                        adapter.updatePaymentList(paymentList);
                    });
                }
            }).start();
        }
    }
}