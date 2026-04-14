package com.example.hospitalsystem.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.hospitalsystem.MyApplication;
import com.example.hospitalsystem.R;
import com.example.hospitalsystem.database.HospitalSysDatabase;
import com.example.hospitalsystem.db_Dao.CommentDao;
import com.example.hospitalsystem.db_Dao.DoctorInfoDao;
import com.example.hospitalsystem.db_Entity.Comment;
import com.example.hospitalsystem.db_Entity.DoctorInfo;
import com.example.hospitalsystem.util.ToastUtil;

import java.text.SimpleDateFormat;
import java.util.List;

public class CommentMineBaseAdapter extends BaseAdapter {

    private final HospitalSysDatabase hospitalSysDB;
    private String user_name;
    private Context mContext;
    private List<Comment> mCommentList;

    public CommentMineBaseAdapter(String user_name, Context mContext, List<Comment> mCommentList) {
        this.user_name = user_name;
        this.mContext = mContext;
        this.mCommentList = mCommentList;
        hospitalSysDB = MyApplication.getInstance().getHospitalDB();
    }

    // 自定义更新评价列表
    public void updateCommentList(List<Comment> commentList) {
        this.mCommentList = commentList;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mCommentList.size();
    }

    @Override
    public Object getItem(int i) {
        return mCommentList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return mCommentList.get(i).getCommentId();
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.comment_find_item_list, null);
            holder = new ViewHolder();
            holder.tv_doctor_name = convertView.findViewById(R.id.tv_doctor_name);
            holder.tv_publish_time = convertView.findViewById(R.id.tv_publish_time);
            holder.tv_state = convertView.findViewById(R.id.tv_state);
            holder.btn_doctor_intro = convertView.findViewById(R.id.btn_doctor_intro);
            holder.btn_view_comment = convertView.findViewById(R.id.btn_view_comment);
            holder.btn_del_comment = convertView.findViewById(R.id.btn_del_comment);
            convertView.setTag(holder);
        }
        else {
            holder =(ViewHolder) convertView.getTag();
        }
        Comment comment = mCommentList.get(i);
        // 发表时间
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String timeStr = sdf.format(comment.getPublishTime());;
        holder.tv_publish_time.setText(timeStr);

        // 审核状态
        holder.tv_state.setText(Comment.stateStr[comment.getAudit()]);

        new Thread(new Runnable() {
            @Override
            public void run() {
                DoctorInfoDao doctorInfoDao = new DoctorInfoDao();
                DoctorInfo doctorInfo = doctorInfoDao.searchByDoctorId(comment.getCommentedId());

                ((Activity) mContext).runOnUiThread(()->{
                    // 医生姓名
                    holder.tv_doctor_name.setText(doctorInfo.getName());

                    // 查看医生介绍按钮
                    holder.btn_doctor_intro.setOnClickListener(v -> {
                        Dialog dialog = new Dialog(mContext);
                        dialog.setContentView(R.layout.dialog_doctor_intro);
                        TextView tv_doctor_id = dialog.findViewById(R.id.tv_doctor_id);
                        TextView tv_name = dialog.findViewById(R.id.tv_name);
                        TextView tv_gender = dialog.findViewById(R.id.tv_gender);
                        TextView tv_seniority = dialog.findViewById(R.id.tv_seniority);
                        TextView tv_intro = dialog.findViewById(R.id.tv_intro);
                        // 设置对话框的信息取值
                        tv_doctor_id.setText(doctorInfo.getDoctorId());
                        tv_name.setText(doctorInfo.getName());
                        tv_gender.setText(doctorInfo.getGender() == DoctorInfo.MALE ? "男" : "女");
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
                        ToastUtil.show(mContext, "这是关于医生" +  doctorInfo.getName() + "的介绍");
                    });

                    // 查看评价信息按钮
                    holder.btn_view_comment.setOnClickListener(v1 -> {
                        Dialog dialog = new Dialog(mContext);
                        dialog.setContentView(R.layout.dialog_comment_info);
                        // 获取控件
                        TextView tv_patient_id = dialog.findViewById(R.id.tv_patient_id);
                        TextView tv_doctor_id = dialog.findViewById(R.id.tv_doctor_id);
                        TextView tv_doctor_name = dialog.findViewById(R.id.tv_doctor_name);
                        TextView tv_publish_time = dialog.findViewById(R.id.tv_publish_time);
                        TextView tv_content = dialog.findViewById(R.id.tv_content);
                        // 设置取值
                        tv_patient_id.setText(comment.getPublisherId());
                        tv_doctor_id.setText(comment.getCommentedId());
                        tv_doctor_name.setText(doctorInfo.getName());
                        tv_publish_time.setText(timeStr);
                        tv_content.setText(comment.getCommentText());
                        // 设置对话框宽度为屏幕宽度的80%
                        Window window = dialog.getWindow();
                        if (window != null) {
                            WindowManager.LayoutParams params = window.getAttributes();
                            params.width = (int) (getScreenWidth(mContext) * 0.8);
                            params.height = (int) (getScreenHeight(mContext) * 0.6);
                            window.setAttributes(params);
                        }
                        dialog.show();
                    });

                    // 删除评论按钮
                    holder.btn_del_comment.setOnClickListener(v2 -> {
                        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                        builder.setTitle("确认删除");
                        builder.setMessage("您确认要删除该则评论吗?");
                        builder.setPositiveButton("确认删除", (dialog, which) -> {
                            // 删除评论
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    CommentDao commentDao = new CommentDao();
                                    long success = commentDao.deleteById(comment.getCommentId());
                                    ((Activity) mContext).runOnUiThread(()->{
                                        if (success > 0) {
                                            ToastUtil.show(mContext, "删除成功");
                                        } else {
                                            ToastUtil.show(mContext, "删除失败");
                                        }
                                        notifyDataSetChanged();
                                    });
                                }
                            }).start();
                        });
                        builder.setNegativeButton("否", null);
                        builder.create().show();
                    });
                });
            }
        }).start();
        return convertView;
    }

    public final class ViewHolder {
        public TextView tv_doctor_name;
        public TextView tv_publish_time;
        public TextView tv_state;
        public android.widget.Button btn_doctor_intro;
        public android.widget.Button btn_view_comment;
        public android.widget.Button btn_del_comment;
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
