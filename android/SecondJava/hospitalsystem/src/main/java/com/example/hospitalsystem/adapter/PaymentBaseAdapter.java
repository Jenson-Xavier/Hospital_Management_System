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
import android.widget.TextView;

import com.example.hospitalsystem.MyApplication;
import com.example.hospitalsystem.R;
import com.example.hospitalsystem.database.HospitalSysDatabase;
import com.example.hospitalsystem.db_Dao.OfficeDao;
import com.example.hospitalsystem.db_Entity.Office;
import com.example.hospitalsystem.db_Entity.Payment;
import com.example.hospitalsystem.util.ToastUtil;

import java.text.SimpleDateFormat;
import java.util.List;

public class PaymentBaseAdapter extends BaseAdapter {

    private final HospitalSysDatabase hospitalSysDB;
    private String user_name;
    private Context mContext;
    private List<Payment> mPaymentList;

    public PaymentBaseAdapter(String user_name, Context mContext, List<Payment> mPaymentList) {
        this.user_name = user_name;
        this.mContext = mContext;
        this.mPaymentList = mPaymentList;
        hospitalSysDB = MyApplication.getInstance().getHospitalDB();
    }

    // 自定义更新支付记录信息列表
    public void updatePaymentList(List<Payment> paymentList) {
        this.mPaymentList = paymentList;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mPaymentList.size();
    }

    @Override
    public Object getItem(int i) {
        return mPaymentList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return mPaymentList.get(i).getPaymentId();
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.payment_item_list, null);
            holder = new ViewHolder();
            holder.tv_payTime = convertView.findViewById(R.id.tv_payTime);
            holder.tv_office = convertView.findViewById(R.id.tv_office);
            holder.tv_payAmount = convertView.findViewById(R.id.tv_payAmount);
            holder.btn_view_payment = convertView.findViewById(R.id.btn_view_payment);
            convertView.setTag(holder);
        }
        else {
            holder = (ViewHolder) convertView.getTag();
        }
        Payment payment = mPaymentList.get(i);

        // 支付日期
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String dataStr = sdf.format(payment.getPayTime());;
        holder.tv_payTime.setText(dataStr);

        // 支付金额
        holder.tv_payAmount.setText(String.valueOf(payment.getPaymentAmount()));

        // 科室
        new Thread(new Runnable() {
            @Override
            public void run() {
                OfficeDao officeDao = new OfficeDao();
                Office office = officeDao.searchByPaymentId(payment.getPaymentId());

                ((Activity) mContext).runOnUiThread(()->{
                    // 设置科室名称
                    holder.tv_office.setText(office.getName());

                });
            }
        }).start();

        // 设置监听
        holder.btn_view_payment.setOnClickListener(v -> {
            // 显示对话框
            Dialog dialog = new Dialog(mContext);
            dialog.setContentView(R.layout.dialog_payment_info);
            // 获取控件
            TextView tv_patient_username = dialog.findViewById(R.id.tv_patient_username);
            TextView tv_payTime = dialog.findViewById(R.id.tv_payTime);
            TextView tv_payAmount = dialog.findViewById(R.id.tv_payAmount);

            // 设置控件信息
            tv_patient_username.setText(user_name);
            tv_payTime.setText(dataStr);
            tv_payAmount.setText(String.valueOf(payment.getPaymentAmount()));

            // 设置对话框宽度为屏幕宽度的80%
            Window window = dialog.getWindow();
            if (window != null) {
                WindowManager.LayoutParams params = window.getAttributes();
                params.width = (int) (getScreenWidth(mContext) * 0.8);
                params.height = (int) (getScreenHeight(mContext) * 0.4);
                window.setAttributes(params);
            }
            dialog.show();
            ToastUtil.show(mContext, "这是支付记录的详细信息");
        });

        return convertView;
    }

    public final class ViewHolder {
        public TextView tv_payTime;
        public TextView tv_office;
        public TextView tv_payAmount;
        public android.widget.Button btn_view_payment;
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
