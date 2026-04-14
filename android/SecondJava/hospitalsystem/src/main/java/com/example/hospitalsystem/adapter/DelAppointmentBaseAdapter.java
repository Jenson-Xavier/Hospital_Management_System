package com.example.hospitalsystem.adapter;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.appcompat.app.AlertDialog;

import com.example.hospitalsystem.MyApplication;
import com.example.hospitalsystem.R;
import com.example.hospitalsystem.database.HospitalSysDatabase;
import com.example.hospitalsystem.db_Dao.AppointmentDao;
import com.example.hospitalsystem.db_Dao.DoctorInfoDao;
import com.example.hospitalsystem.db_Dao.OfficeDao;
import com.example.hospitalsystem.db_Dao.PaymentDao;
import com.example.hospitalsystem.db_Entity.Appointment;
import com.example.hospitalsystem.db_Entity.DoctorInfo;
import com.example.hospitalsystem.db_Entity.Office;
import com.example.hospitalsystem.db_Entity.Payment;
import com.example.hospitalsystem.util.ToastUtil;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class DelAppointmentBaseAdapter extends BaseAdapter {

    private final HospitalSysDatabase hospitalSysDB;
    private String user_name;
    private Context mContext;
    private List<Appointment> mAppointmentList;

    public DelAppointmentBaseAdapter(Context mContext, List<Appointment> mAppointmentList, String user_name) {
        this.mContext = mContext;
        this.mAppointmentList = mAppointmentList;
        this.user_name = user_name;
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
            convertView = LayoutInflater.from(mContext).inflate(R.layout.appointment_del_item_list, null);
            holder = new ViewHolder();
            holder.tv_doctor = convertView.findViewById(R.id.tv_doctor);
            holder.tv_period = convertView.findViewById(R.id.tv_period);
            holder.tv_date = convertView.findViewById(R.id.tv_date);
            holder.tv_office = convertView.findViewById(R.id.tv_office);
            holder.tv_payAmount = convertView.findViewById(R.id.tv_payAmount);
            holder.tv_state = convertView.findViewById(R.id.tv_state);
            holder.btn_del_appoint = convertView.findViewById(R.id.btn_del_appoint);
            convertView.setTag(holder);
        }
        else {
            holder = (ViewHolder) convertView.getTag();
        }
        Appointment appointment = mAppointmentList.get(i);

        // 就诊时段
        int appointmentPeriod = appointment.getVisitPeriod();
        holder.tv_period.setText(Appointment.periodStr[appointmentPeriod]);
        // 就诊日期 首先将日期从Date类型转化为String类型
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String dateStr = sdf.format(appointment.getVisitDate());;
        holder.tv_date.setText(dateStr);
        // 预约状态
        holder.tv_state.setText(Appointment.stateStr[appointment.getOrderState()]);

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
                });
            }
        }).start();

        // 预约状态只有为<待看诊>时才可以取消预约
        if (appointment.getOrderState() != Appointment.WAIT) {
            // 置为不可点击
            holder.btn_del_appoint.setClickable(false);
            holder.btn_del_appoint.setBackground(mContext.getResources().getDrawable(R.drawable.btn_rect_3));
            holder.btn_del_appoint.setText("无法取消");
        }
        else {
            // 为取消预约按钮设置监听
            holder.btn_del_appoint.setOnClickListener(v->{
                // 只有在就诊日之前才可以取消
                Date currentDate = new Date();
                if (!currentDate.before(appointment.getVisitDate())) {
                    ToastUtil.show(mContext, "抱歉，您只能在就诊日之前取消预约");
                    return;
                }
                // 确认对话框
                AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setTitle("取消预约");
                builder.setMessage("请问您确认取消本次预约吗?");
                builder.setPositiveButton("确认取消", (dialog, which) -> {
                    Appointment new_appointment = mAppointmentList.get(i);
                    new_appointment.setOrderState(Appointment.CANCELLED);

                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            AppointmentDao appointmentDao = new AppointmentDao();
                            long success = appointmentDao.update(new_appointment);
                            // 返回主线程
                            ((Activity) mContext).runOnUiThread(()->{
                                if (success > 0) {
                                    ToastUtil.show(mContext, "您已成功取消预约");
                                } else {
                                    ToastUtil.show(mContext, "取消预约失败");
                                }
                                notifyDataSetChanged();
                            });
                        }
                    }).start();
                });
                builder.setNegativeButton("否", null);
                builder.create().show();
            });
        }
        return convertView;
    }

    public final class ViewHolder {
        public TextView tv_doctor;
        public TextView tv_period;
        public TextView tv_date;
        public TextView tv_office;
        public TextView tv_payAmount;
        public TextView tv_state;
        public android.widget.Button btn_del_appoint;
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
