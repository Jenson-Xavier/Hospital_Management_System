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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.hospitalsystem.adapter.CommentManageBaseAdapter;
import com.example.hospitalsystem.adapter.CommentMineBaseAdapter;
import com.example.hospitalsystem.database.HospitalSysDatabase;
import com.example.hospitalsystem.db_Dao.CommentDao;
import com.example.hospitalsystem.db_Dao.OfficeDao;
import com.example.hospitalsystem.db_Entity.Comment;
import com.example.hospitalsystem.db_Entity.Office;
import com.example.hospitalsystem.util.ToastUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;


public class CommentMineFragment extends Fragment implements View.OnClickListener, TextView.OnEditorActionListener {

    private String user_name;
    private HospitalSysDatabase hospitalSysDB;
    private TextView tv_date;
    private TextView tv_nomatch;
    private ImageView iv_search;
    private ListView lv_comment;
    private Spinner sp_office;
    private Spinner sp_state;
    private EditText et_doctor;
    private CommentMineBaseAdapter adapter;
    private Button btn_add_comment;

    public CommentMineFragment() {
        // Required empty public constructor
    }


    public static CommentMineFragment newInstance(String user_name) {
        CommentMineFragment fragment = new CommentMineFragment();
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
        View view = inflater.inflate(R.layout.fragment_comment_mine, container, false);

        // 获取参数
        user_name = getArguments().getString("user_name");
        hospitalSysDB = MyApplication.getInstance().getHospitalDB();

        // 获取布局控件
        TextView tv_title = view.findViewById(R.id.tv_title);
        ImageView iv_back = view.findViewById(R.id.iv_back);
        ImageView iv_clear_text = view.findViewById(R.id.iv_clear_text);
        tv_date = view.findViewById(R.id.tv_date);
        sp_office = view.findViewById(R.id.sp_office);
        sp_state = view.findViewById(R.id.sp_state);
        et_doctor = view.findViewById(R.id.et_doctor);
        iv_search = view.findViewById(R.id.iv_search);
        tv_nomatch = view.findViewById(R.id.tv_nomatch);
        lv_comment = view.findViewById(R.id.lv_comment);
        btn_add_comment = view.findViewById(R.id.btn_add_comment);

        // 设置标题
        tv_title.setText("我的评论");
        iv_back.setOnClickListener(v -> {
            getActivity().getSupportFragmentManager().popBackStack();
        });

        // 新增评论，需要切换到查询医生界面
        btn_add_comment.setOnClickListener(v -> {
            // 在此传递参数
            Fragment fragment = DoctorFindFragment.newInstance(user_name, "comment");
            // 切换活动中的fragment
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .addToBackStack(null)
                    .commit();
            ToastUtil.show(getActivity(), "请您选择医生进行评论");
        });

        // 设置日期
        tv_date.setOnClickListener(this);
        iv_clear_text.setOnClickListener(this);

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

                // 查询所有审核状态通过的评论记录
                CommentDao commentDao = new CommentDao();
                List<Comment> commentList = commentDao.searchByPatientId(user_name);

                // 返回主线程
                getActivity().runOnUiThread(()->{
                    // 设置科室下拉列表适配器
                    ArrayAdapter<String> startAdapter = new ArrayAdapter<>(getActivity(), R.layout.office_item_select, officeNames);
                    sp_office.setPrompt("请选择科室");
                    sp_office.setAdapter(startAdapter);
                    sp_office.setSelection(0);

                    if (commentList != null && !commentList.isEmpty()) {
                        adapter = new CommentMineBaseAdapter(user_name, getActivity(), commentList);
                        lv_comment.setAdapter(adapter);
                        tv_nomatch.setVisibility(View.GONE);
                        lv_comment.setVisibility(View.VISIBLE);
                    } else {
                        tv_nomatch.setVisibility(View.VISIBLE);
                        lv_comment.setVisibility(View.GONE);
                    }
                });

            }
        }).start();

        // 选择审核状态
        String[] stateStr = Comment.stateStr;
        ArrayAdapter<String> stateAdapter = new ArrayAdapter<>(getActivity(), R.layout.audit_state_item_select, stateStr);
        sp_office.setPrompt("请选择审核状态");
        sp_state.setAdapter(stateAdapter);
        sp_office.setSelection(0);

        // 搜索医生
        et_doctor.setOnEditorActionListener(this);
        iv_search.setOnClickListener(this);

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
        if (v.getId() == R.id.tv_date) {
            showDatePicker();
        }
        else if (v.getId() == R.id.iv_clear_text) {
            tv_date.setText("");
        }
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

            // 获取医生姓名
            String doctor_name = et_doctor.getText().toString();

            // 获取审核状态
            String audit_state = sp_state.getSelectedItem().toString();
            String[] stateStr = Comment.stateStr;
            int StateIndex = Arrays.asList(stateStr).indexOf(audit_state);

            // 获取科室名称+id
            String office_name = sp_office.getSelectedItem().toString();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    OfficeDao officeDao = new OfficeDao();
                    List<Office> officeList = officeDao.searchByName(office_name);
                    int office_id = office_name.equals("不限") ? -1 : officeList.get(0).getOfficeId();

                    // 根据科室id、医生姓名、审核状态和日期模糊查询评论记录
                    CommentDao commentDao = new CommentDao();
                    List<Comment> commentList = commentDao.searchByPatientId(user_name, StateIndex, doctor_name, office_id, finalDateStr);

                    // 返回主线程
                    getActivity().runOnUiThread(()->{
                        if (commentList != null && !commentList.isEmpty()) {
                            tv_nomatch.setVisibility(View.GONE);
                            lv_comment.setVisibility(View.VISIBLE);
                        } else {
                            tv_nomatch.setVisibility(View.VISIBLE);
                            lv_comment.setVisibility(View.GONE);
                        }
                        adapter.updateCommentList(commentList);
                    });
                }
            }).start();
        }
    }

    @Override
    public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
        if (i == EditorInfo.IME_ACTION_SEARCH) {
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

            // 获取医生姓名
            String doctor_name = et_doctor.getText().toString();

            // 获取审核状态
            String audit_state = sp_state.getSelectedItem().toString();
            String[] stateStr = Comment.stateStr;
            int StateIndex = Arrays.asList(stateStr).indexOf(audit_state);

            // 获取科室名称+id
            String office_name = sp_office.getSelectedItem().toString();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    OfficeDao officeDao = new OfficeDao();
                    List<Office> officeList = officeDao.searchByName(office_name);
                    int office_id = office_name.equals("不限") ? -1 : officeList.get(0).getOfficeId();

                    // 根据科室id、医生姓名、审核状态和日期模糊查询评论记录
                    CommentDao commentDao = new CommentDao();
                    List<Comment> commentList = commentDao.searchByPatientId(user_name, StateIndex, doctor_name, office_id, finalDateStr);

                    // 返回主线程
                    getActivity().runOnUiThread(()->{
                        if (commentList != null && !commentList.isEmpty()) {
                            tv_nomatch.setVisibility(View.GONE);
                            lv_comment.setVisibility(View.VISIBLE);
                        } else {
                            tv_nomatch.setVisibility(View.VISIBLE);
                            lv_comment.setVisibility(View.GONE);
                        }
                        adapter.updateCommentList(commentList);
                    });
                }
            }).start();
            return true;
        }
        return false;
    }

}