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
import com.example.hospitalsystem.db_Dao.DoctorInfoDao;
import com.example.hospitalsystem.db_Dao.OfficeDao;
import com.example.hospitalsystem.db_Dao.PaymentDao;
import com.example.hospitalsystem.db_Entity.Appointment;
import com.example.hospitalsystem.db_Entity.DoctorInfo;
import com.example.hospitalsystem.db_Entity.Office;
import com.example.hospitalsystem.db_Entity.Payment;
import com.example.hospitalsystem.util.ToastUtil;

import java.text.SimpleDateFormat;
import java.util.List;

public class SearchAppointmentBaseAdapter extends BaseAdapter {

    private final HospitalSysDatabase hospitalSysDB;
    private String user_name;
    private Context mContext;
    private List<Appointment> mAppointmentList;

    public SearchAppointmentBaseAdapter(String user_name, Context mContext, List<Appointment> mAppointmentList) {
        this.user_name = user_name;
        this.mContext = mContext;
        this.mAppointmentList = mAppointmentList;
        hospitalSysDB = MyApplication.getInstance().getHospitalDB();
    }

    // 自定义更新预约记录信息列表
    public void updateAppointmentList(List<Appointment> appointmentList) {
        this.mAppointmentList = appointmentList;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mAppointmentList.size();
    }

    @Override
    public Object getItem(int i) {
        return mAppointmentList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return mAppointmentList.get(i).getAppointmentId();
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.appointment_search_item_list, null);
            holder = new ViewHolder();
            holder.tv_doctor = convertView.findViewById(R.id.tv_doctor);
            holder.tv_period = convertView.findViewById(R.id.tv_period);
            holder.tv_date = convertView.findViewById(R.id.tv_date);
            holder.tv_office = convertView.findViewById(R.id.tv_office);
            holder.tv_payAmount = convertView.findViewById(R.id.tv_payAmount);
            holder.tv_state = convertView.findViewById(R.id.tv_state);
            holder.btn_view_appoint = convertView.findViewById(R.id.btn_view_appoint);
            convertView.setTag(holder);
        }
        else {
            holder = (ViewHolder) convertView.getTag();
        }
        Appointment appointment = mAppointmentList.get(i);

        // 就诊日期 首先将日期从Date类型转化为String类型
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String dateStr = sdf.format(appointment.getVisitDate());;
        holder.tv_date.setText(dateStr);
        // 预约状态
        holder.tv_state.setText(Appointment.stateStr[appointment.getOrderState()]);
        // 就诊时段
        int appointmentPeriod = appointment.getVisitPeriod();
        holder.tv_period.setText(Appointment.periodStr[appointmentPeriod]);

        new Thread(new Runnable() {
            @Override
            public void run() {
                // 根据医生id查询医生信息
                DoctorInfoDao doctorInfoDao = new DoctorInfoDao();
                DoctorInfo doctorInfo = doctorInfoDao.searchByDoctorId(appointment.getDoctorId());

                // 根据科室id查询科室信息
                OfficeDao officeDao = new OfficeDao();
                Office office = officeDao.searchById(doctorInfo.getOfficeId());

                // 根据预约记录id查询支付记录
                PaymentDao paymentDao = new PaymentDao();
                Payment payment = paymentDao.searchByAppointmentId(appointment.getAppointmentId());

                // 返回主线程
                ((Activity) mContext).runOnUiThread(()->{
                    // 设置医生姓名
                    holder.tv_doctor.setText(doctorInfo.getName());

                    // 设置科室名称
                    holder.tv_office.setText(office.getName());

                    // 设置支付金额
                    holder.tv_payAmount.setText(String.valueOf(payment.getPaymentAmount()));

                    // 为查看按钮设置监听
                    holder.btn_view_appoint.setOnClickListener(v -> {
                        // 显示对话框
                        Dialog dialog = new Dialog(mContext);
                        dialog.setContentView(R.layout.dialog_view_appointment_info);
                        // 获取控件
                        TextView tv_patient_username_view = dialog.findViewById(R.id.tv_patient_username_view);
                        TextView tv_doctor_view = dialog.findViewById(R.id.tv_doctor_view);
                        TextView tv_office_view = dialog.findViewById(R.id.tv_office_view);
                        TextView tv_date_view = dialog.findViewById(R.id.tv_date_view);
                        TextView tv_period_view = dialog.findViewById(R.id.tv_period_view);
                        TextView tv_serialNum_view = dialog.findViewById(R.id.tv_serialNum_view);
                        TextView tv_price_view = dialog.findViewById(R.id.tv_price_view);
                        TextView tv_orderTime_view = dialog.findViewById(R.id.tv_orderTime_view);
                        TextView tv_state_view = dialog.findViewById(R.id.tv_state_view);

                        // 设置控件信息
                        tv_patient_username_view.setText(user_name);
                        tv_doctor_view.setText(doctorInfo.getName());
                        tv_office_view.setText(office.getName());
                        tv_date_view.setText(dateStr);
                        tv_period_view.setText(Appointment.periodStr[appointment.getVisitPeriod()]);
                        tv_serialNum_view.setText(String.valueOf(appointment.getSerialNum()));
                        tv_price_view.setText(String.valueOf(payment.getPaymentAmount()));
                        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        String timeStr = sdf1.format(appointment.getOrderTime());
                        tv_orderTime_view.setText(timeStr);
                        tv_state_view.setText(Appointment.stateStr[appointment.getOrderState()]);

                        // 设置对话框宽度为屏幕宽度的80%
                        Window window = dialog.getWindow();
                        if (window != null) {
                            WindowManager.LayoutParams params = window.getAttributes();
                            params.width = (int) (getScreenWidth(mContext) * 0.8);
                            params.height = (int) (getScreenHeight(mContext) * 0.8);
                            window.setAttributes(params);
                        }
                        dialog.show();
                        ToastUtil.show(mContext, "这是预约记录的详细信息");
                    });
                });
            }
        }).start();

        return convertView;
    }

    public final class ViewHolder {
        public TextView tv_doctor;
        public TextView tv_period;
        public TextView tv_date;
        public TextView tv_office;
        public TextView tv_payAmount;
        public TextView tv_state;
        public android.widget.Button btn_view_appoint;
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



