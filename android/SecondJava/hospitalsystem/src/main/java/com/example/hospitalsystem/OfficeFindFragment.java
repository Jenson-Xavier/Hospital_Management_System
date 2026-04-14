package com.example.hospitalsystem;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.hospitalsystem.adapter.OfficeBaseAdapter;
import com.example.hospitalsystem.database.HospitalSysDatabase;
import com.example.hospitalsystem.db_Dao.OfficeDao;
import com.example.hospitalsystem.db_Entity.Office;
import com.example.hospitalsystem.util.ToastUtil;

import java.util.List;


public class OfficeFindFragment extends Fragment implements AdapterView.OnItemClickListener, TextView.OnEditorActionListener {


    private String user_name;
    private HospitalSysDatabase hospitalSysDB;
    private List<Office> mOfficeInfos;
    private EditText et_find_office;
    private ImageView iv_back;
    private TextView tv_nomatch;
    private ListView lv_office;
    private OfficeBaseAdapter adapter;

    public OfficeFindFragment() {
        // Required empty public constructor
    }


    public static OfficeFindFragment newInstance(String user_name) {
        OfficeFindFragment fragment = new OfficeFindFragment();
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
        View view = inflater.inflate(R.layout.fragment_office_find, container, false);

        // 获取参数
        user_name = getArguments().getString("user_name");
        hospitalSysDB = MyApplication.getInstance().getHospitalDB();

        et_find_office = view.findViewById(R.id.et_find_office);
        // 为EditText控件设置键盘事件监听，按下回车键后执行查询操作
        et_find_office.setOnEditorActionListener(this);
        // 修改标题
        TextView tv_title = view.findViewById(R.id.tv_title);
        tv_title.setText("查询科室信息");
        // 返回按钮
        iv_back = view.findViewById(R.id.iv_back);
        iv_back.setOnClickListener(v -> {
            getActivity().getSupportFragmentManager().popBackStack();
        });
        tv_nomatch = view.findViewById(R.id.tv_nomatch);
        lv_office = view.findViewById(R.id.lv_office);
        lv_office.setOnItemClickListener(this);

        // 启动子线程查询科室
        new Thread(new Runnable() {
            @Override
            public void run() {
                OfficeDao officeDao = new OfficeDao();
                mOfficeInfos = officeDao.searchAll();
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (mOfficeInfos != null && !mOfficeInfos.isEmpty()) {
                            adapter = new OfficeBaseAdapter(getActivity(), mOfficeInfos, user_name);
                            lv_office.setAdapter(adapter);
                            tv_nomatch.setVisibility(View.GONE);
                        }
                        else {
                            // 如果没有数据，显示“无匹配”提示
                            tv_nomatch.setVisibility(View.VISIBLE);
                        }
                    }
                });
            }
        }).start();

        return view;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        ToastUtil.show(getActivity(), "您选择的是:" + mOfficeInfos.get(i).getName());
        String office_name = mOfficeInfos.get(i).getName();
        int office_id = mOfficeInfos.get(i).getOfficeId();
        // 切换到对应的排版信息界面
        Fragment fragment = AppointFindScheduleFragment.newInstance(user_name, office_name, office_id);
        getActivity().getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit();
    }

    @Override
    public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
        if (i == EditorInfo.IME_ACTION_SEARCH) {
            String name = et_find_office.getText().toString();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    OfficeDao officeDao = new OfficeDao();
                    mOfficeInfos = officeDao.searchByName(name);
                    getActivity().runOnUiThread(()->{
                        if (mOfficeInfos != null && !mOfficeInfos.isEmpty()) {
                            tv_nomatch.setVisibility(View.GONE);
                            lv_office.setVisibility(View.VISIBLE);
                        }
                        else {
                            tv_nomatch.setVisibility(View.VISIBLE);
                            lv_office.setVisibility(View.GONE);
                        }
                        adapter.updateOfficeList(mOfficeInfos);
                    });
                }
            }).start();
            return true;
        }
        return false;
    }
}