package com.example.hospitalsystem;

import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.hospitalsystem.adapter.CommentManageBaseAdapter;
import com.example.hospitalsystem.adapter.SearchAppointmentBaseAdapter;
import com.example.hospitalsystem.database.HospitalSysDatabase;
import com.example.hospitalsystem.db_Dao.AppointmentDao;
import com.example.hospitalsystem.db_Dao.CommentDao;
import com.example.hospitalsystem.db_Dao.OfficeDao;
import com.example.hospitalsystem.db_Entity.Appointment;
import com.example.hospitalsystem.db_Entity.Comment;
import com.example.hospitalsystem.db_Entity.Office;

import java.util.List;
import java.util.stream.Collectors;


public class CommentManageFragment extends Fragment implements View.OnClickListener, TextView.OnEditorActionListener {


    private String user_name;
    private HospitalSysDatabase hospitalSysDB;
    private List<String> officeNameList;
    private Spinner sp_office;
    private EditText et_doctor;
    private ImageView iv_search;
    private TextView tv_nomatch;
    private ListView lv_comment;
    private CommentManageBaseAdapter adapter;
    private Button btn_mine;

    public CommentManageFragment() {
        // Required empty public constructor
    }

    public static CommentManageFragment newInstance(String user_name) {
        CommentManageFragment fragment = new CommentManageFragment();
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
        View view = inflater.inflate(R.layout.fragment_comment_manage, container, false);

        // 获取参数
        user_name = getArguments().getString("user_name");
        hospitalSysDB = MyApplication.getInstance().getHospitalDB();

        // 获取控件
        TextView tv_title = view.findViewById(R.id.tv_title);
        ImageView iv_back = view.findViewById(R.id.iv_back);
        sp_office = view.findViewById(R.id.sp_office);
        et_doctor = view.findViewById(R.id.et_doctor);
        iv_search = view.findViewById(R.id.iv_search);
        tv_nomatch = view.findViewById(R.id.tv_nomatch);
        lv_comment = view.findViewById(R.id.lv_comment);
        btn_mine = view.findViewById(R.id.btn_mine);

        // 设置标题
        tv_title.setText("评价社区");
        iv_back.setOnClickListener(v -> {
            getActivity().getSupportFragmentManager().popBackStack();
        });

        // 切换至我的评价
        btn_mine.setOnClickListener(v -> {
            Fragment fragment = CommentMineFragment.newInstance(user_name);
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .addToBackStack(null)
                    .commit();
        });

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
                List<Comment> commentList = commentDao.searchPassed();

                // 返回主线程
                getActivity().runOnUiThread(()->{
                    // 设置科室下拉列表适配器
                    ArrayAdapter<String> startAdapter = new ArrayAdapter<>(getActivity(), R.layout.office_item_select, officeNames);
                    sp_office.setPrompt("请选择科室");
                    sp_office.setAdapter(startAdapter);
                    sp_office.setSelection(0);

                    if (commentList != null && !commentList.isEmpty()) {
                        adapter = new CommentManageBaseAdapter(user_name, getActivity(), commentList);
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

        // 选择医生
        et_doctor.setOnEditorActionListener(this);
        iv_search.setOnClickListener(this);

        return view;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.iv_search) {
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

                    // 根据科室id、医生姓名模糊查询评论记录
                    CommentDao commentDao = new CommentDao();
                    List<Comment> commentList = commentDao.searchByOfficeIdAndDoctorName(office_id, doctor_name);

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

                    // 根据科室id、医生姓名模糊查询评论记录
                    CommentDao commentDao = new CommentDao();
                    List<Comment> commentList = commentDao.searchByOfficeIdAndDoctorName(office_id, doctor_name);

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