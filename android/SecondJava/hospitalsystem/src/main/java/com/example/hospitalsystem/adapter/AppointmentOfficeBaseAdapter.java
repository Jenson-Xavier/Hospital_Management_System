package com.example.hospitalsystem.adapter;

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

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.example.hospitalsystem.AppointFindScheduleFragment;
import com.example.hospitalsystem.R;
import com.example.hospitalsystem.db_Entity.Office;
import com.example.hospitalsystem.util.ToastUtil;

import java.util.List;

public class AppointmentOfficeBaseAdapter extends BaseAdapter {

    private Context mContext;
    private List<Office> mOfficeInfos;
    private String user_name;

    public AppointmentOfficeBaseAdapter(Context ctx, List<Office> infos, String user_name) {
        this.mContext = ctx;
        this.mOfficeInfos = infos;
        this.user_name = user_name;
    }

    // 自定义添加item
    public void add(Office info) {
        mOfficeInfos.add(info);
        notifyDataSetChanged();
    }

    // 自定义移除item
    public void remove(Office info) {
        mOfficeInfos.remove(info);
        notifyDataSetChanged();
    }

    // 自定义更新科室信息列表
    public void updateOfficeList(List<Office> officeList) {
        this.mOfficeInfos = officeList;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mOfficeInfos.size();
    }

    @Override
    public Object getItem(int i) {
        return mOfficeInfos.get(i);
    }

    @Override
    public long getItemId(int i) {
        return mOfficeInfos.get(i).getOfficeId();
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.appoint_office_item_list, null);
            holder = new ViewHolder();
            holder.tv_office_name = convertView.findViewById(R.id.tv_office_name);
            holder.btn_office_intro = convertView.findViewById(R.id.btn_office_intro);
            holder.btn_select_office = convertView.findViewById(R.id.btn_select_office);
            convertView.setTag(holder);
        }
        else {
            holder = (ViewHolder) convertView.getTag();
        }
        Office office = mOfficeInfos.get(i);
        holder.tv_office_name.setText(mOfficeInfos.get(i).getName());
        holder.btn_office_intro.setOnClickListener(v -> {
            ToastUtil.show(mContext, "这是关于" + mOfficeInfos.get(i).getName() + "的介绍");
            // 显示对话框
            Dialog dialog = new Dialog(mContext);
            dialog.setContentView(R.layout.dialog_office_intro);
            // 获取控件
            TextView tv_name = dialog.findViewById(R.id.tv_name);
            TextView tv_intro = dialog.findViewById(R.id.tv_intro);
            tv_name.setText(office.getName());
            tv_intro.setText(office.getIntro());

            // 设置对话框宽度为屏幕宽度的80%
            Window window = dialog.getWindow();
            if (window != null) {
                WindowManager.LayoutParams params = window.getAttributes();
                params.width = (int) (getScreenWidth(mContext) * 0.8);
                params.height = (int) (getScreenHeight(mContext) * 0.4);
                window.setAttributes(params);
            }
            dialog.show();
        });
        holder.btn_select_office.setOnClickListener(v -> {
            ToastUtil.show(mContext, "您已选择科室" + mOfficeInfos.get(i).getName() + ", 现在请您进行预约");
            String office_name = mOfficeInfos.get(i).getName();
            int office_id = mOfficeInfos.get(i).getOfficeId();
            // 跳转到排班信息查询界面
            Fragment fragment = AppointFindScheduleFragment.newInstance(user_name, office_name, office_id);
            ((FragmentActivity) mContext).getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .addToBackStack(null)
                    .commit();
        });
        return convertView;
    }

    public final class ViewHolder {
        public TextView tv_office_name;
        public android.widget.Button btn_office_intro;
        public android.widget.Button btn_select_office;
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
