package com.example.hospitalsystem.adapter;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.example.hospitalsystem.DoctorInfoFindScheduleFragment;
import com.example.hospitalsystem.MyApplication;
import com.example.hospitalsystem.R;
import com.example.hospitalsystem.database.HospitalSysDatabase;
import com.example.hospitalsystem.db_Dao.CommentDao;
import com.example.hospitalsystem.db_Dao.OfficeDao;
import com.example.hospitalsystem.db_Entity.Comment;
import com.example.hospitalsystem.db_Entity.DoctorInfo;
import com.example.hospitalsystem.db_Entity.Office;
import com.example.hospitalsystem.util.ToastUtil;

import java.util.Date;
import java.util.List;

public class DoctorInfoBaseAdapter extends BaseAdapter {

    String user_name;
    Context mContext;
    List<DoctorInfo> mDoctorInfoList;
    HospitalSysDatabase hospitalSysDB;
    String type;

    static final String COMMENT = "comment";
    static final String APPOINT = "appoint";

    public DoctorInfoBaseAdapter(String user_name, Context mContext, List<DoctorInfo> mDoctorInfoList, String type) {
        this.user_name = user_name;
        this.mContext = mContext;
        this.mDoctorInfoList = mDoctorInfoList;
        this.type = type;
        hospitalSysDB = MyApplication.getInstance().getHospitalDB();
    }

    // 自定义更新医生信息列表
    public void updateOfficeList(List<DoctorInfo> doctorInfoList) {
        this.mDoctorInfoList = doctorInfoList;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mDoctorInfoList.size();
    }

    @Override
    public Object getItem(int i) {
        return mDoctorInfoList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return mDoctorInfoList.get(i).getDoctorInfoId();
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.doctorinfo_item_list, null);
            holder = new ViewHolder();
            holder.tv_office = convertView.findViewById(R.id.tv_office);
            holder.tv_gender = convertView.findViewById(R.id.tv_gender);
            holder.tv_seniority = convertView.findViewById(R.id.tv_seniority);
            holder.tv_doctor_name = convertView.findViewById(R.id.tv_doctor_name);
            holder.tv_doctor_id = convertView.findViewById(R.id.tv_doctor_id);
            holder.btn_doctor_intro = convertView.findViewById(R.id.btn_doctor_intro);
            holder.btn_appoint = convertView.findViewById(R.id.btn_appoint);
            convertView.setTag(holder);
        }
        else {
            holder = (ViewHolder) convertView.getTag();
        }
        DoctorInfo doctorInfo = mDoctorInfoList.get(i);
        // 为控件设置取值
        // 医生工号
        holder.tv_doctor_id.setText(doctorInfo.getDoctorId());
        // 医生姓名
        holder.tv_doctor_name.setText(doctorInfo.getName());
        // 性别
        holder.tv_gender.setText(DoctorInfo.ganderStr[doctorInfo.getGender()]);
        // 工龄
        holder.tv_seniority.setText(String.valueOf(doctorInfo.getSeniority()));

        new Thread(new Runnable() {
            @Override
            public void run() {
                // 查询科室名称
                OfficeDao officeDao = new OfficeDao();
                Office office = officeDao.searchById(doctorInfo.getOfficeId());

                // 返回主线程
                ((Activity) mContext).runOnUiThread(()->{
                    // 科室名称
                    holder.tv_office.setText(office.getName());
                });
            }
        }).start();

        // 修改按钮名称
        if (type.equals(COMMENT)) {
            holder.btn_appoint.setText("评论医生");
            holder.btn_appoint.setBackground(mContext.getResources().getDrawable(R.drawable.btn_rect_blue));
        }

        // 医生介绍
        holder.btn_doctor_intro.setOnClickListener(v -> {
            // 显示对话框
            Dialog dialog = new Dialog(mContext);
            dialog.setContentView(R.layout.dialog_doctor_intro);
            // 获取控件
            TextView tv_doctor_id = dialog.findViewById(R.id.tv_doctor_id);
            TextView tv_name = dialog.findViewById(R.id.tv_name);
            TextView tv_gender = dialog.findViewById(R.id.tv_gender);
            TextView tv_seniority = dialog.findViewById(R.id.tv_seniority);
            TextView tv_intro = dialog.findViewById(R.id.tv_intro);
            // 设置对话框的信息取值
            tv_doctor_id.setText(doctorInfo.getDoctorId());
            tv_name.setText(doctorInfo.getName());
            tv_gender.setText(DoctorInfo.ganderStr[doctorInfo.getGender()]);
            tv_seniority.setText(String.valueOf(doctorInfo.getSeniority()));
            tv_intro.setText(doctorInfo.getIntro());

            // 设置对话框宽度为屏幕宽度的80%
            Window window = dialog.getWindow();
            if (window != null) {
                WindowManager.LayoutParams params = window.getAttributes();
                params.width = (int) (getScreenWidth(mContext) * 0.8);
                params.height = (int) (getScreenHeight(mContext) * 0.6);
                window.setAttributes(params);
            }
            dialog.show();
            ToastUtil.show(mContext, "这是关于医生" + doctorInfo.getName() + "的介绍");
        });

        // 点击预约
        holder.btn_appoint.setOnClickListener(v1 -> {
            // 预约
            if (type.equals(APPOINT)){
                ToastUtil.show(mContext, "您已进入医生" + doctorInfo.getName() + "的预约界面");
                Fragment fragment = DoctorInfoFindScheduleFragment.newInstance(user_name, doctorInfo.getDoctorId(), doctorInfo.getName());
                ((FragmentActivity) mContext).getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, fragment)
                        .addToBackStack(null)
                        .commit();
            }
            // 评论
            else if (type.equals(COMMENT)) {
                ToastUtil.show(mContext, "您已进入医生" + doctorInfo.getName() + "的评论界面");
                Dialog dialog = new Dialog(mContext);
                dialog.setContentView(R.layout.dialog_comment_add);
                // 获取控件
                TextView tv_doctor_id = dialog.findViewById(R.id.tv_doctor_id);
                TextView tv_doctor_name = dialog.findViewById(R.id.tv_doctor_name);
                EditText et_comment_content = dialog.findViewById(R.id.et_comment_content);
                android.widget.Button btn_back = dialog.findViewById(R.id.btn_back);
                android.widget.Button btn_add_comment = dialog.findViewById(R.id.btn_add_comment);
                // 设置取值
                tv_doctor_id.setText(doctorInfo.getDoctorId());
                tv_doctor_name.setText(doctorInfo.getName());

                // 返回按钮
                btn_back.setOnClickListener(v -> {
                    ToastUtil.show(mContext, "返回成功");
                    dialog.cancel();
                });

                // 确认添加评论
                btn_add_comment.setOnClickListener(v -> {
                    String content = et_comment_content.getText().toString();
                    Comment comment = new Comment();
                    comment.setPublisherId(user_name);
                    comment.setCommentedId(doctorInfo.getDoctorId());
                    comment.setPublishTime(new Date(System.currentTimeMillis()));
                    comment.setCommentText(content);
                    comment.setAudit(Comment.WAIT);
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            CommentDao commentDao = new CommentDao();
                            long generated_id = commentDao.insert(comment);
                            if (generated_id > 0) {
                                ((Activity) mContext).runOnUiThread(()->{
                                    ToastUtil.show(mContext, "成功增加一条评价");
                                });
                            } else {
                                ((Activity) mContext).runOnUiThread(()->{
                                    ToastUtil.show(mContext, "新增评价失败");
                                });
                            }
                            dialog.cancel();
                        }
                    }).start();
                });

                // 设置对话框宽度为屏幕宽度的80%
                Window window = dialog.getWindow();
                if (window != null) {
                    WindowManager.LayoutParams params = window.getAttributes();
                    params.width = (int) (getScreenWidth(mContext) * 0.8);
                    params.height = (int) (getScreenHeight(mContext) * 0.6);
                    window.setAttributes(params);
                }
                dialog.show();
            }
        });

        return convertView;
    }

    public final class ViewHolder {
        public TextView tv_office;
        public TextView tv_doctor_name;
        public TextView tv_gender;
        public TextView tv_seniority;
        public TextView tv_doctor_id;
        public android.widget.Button btn_doctor_intro;
        public android.widget.Button btn_appoint;
    }

    // 获取屏幕宽度的工具方法
    private int getScreenWidth(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        if (wm != null) {
            DisplayMetrics outMetrics = new DisplayMetrics();
            wm.getDefaultDisplay().getMetrics(outMetrics);
            return outMetrics.widthPixels;
        }
        return 0;
    }

    private int getScreenHeight(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        if (wm != null) {
            DisplayMetrics outMetrics = new DisplayMetrics();
            wm.getDefaultDisplay().getMetrics(outMetrics);
            return outMetrics.heightPixels;
        }
        return 0;
    }
}
