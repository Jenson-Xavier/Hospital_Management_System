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
import com.example.hospitalsystem.db_Dao.AppointmentDao;
import com.example.hospitalsystem.db_Dao.OfficeDao;
import com.example.hospitalsystem.db_Dao.PaymentDao;
import com.example.hospitalsystem.db_Entity.Appointment;
import com.example.hospitalsystem.db_Entity.Office;
import com.example.hospitalsystem.db_Entity.Payment;
import com.example.hospitalsystem.db_Entity.Schedule;
import com.example.hospitalsystem.util.ToastUtil;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class DoctorInfoScheduleBaseAdapter extends BaseAdapter {

    private final HospitalSysDatabase hospitalSysDB;
    private String user_name;
    private String doctor_id;
    private String doctor_name;
    private Context mContext;
    private List<Schedule> mScheduleList;
    private float mPrice = 20;

    public DoctorInfoScheduleBaseAdapter(String user_name, String doctor_id, Context mContext, String doctor_name, List<Schedule> mAppointmentList) {
        this.user_name = user_name;
        this.doctor_id = doctor_id;
        this.mContext = mContext;
        this.doctor_name = doctor_name;
        this.mScheduleList = mAppointmentList;
        hospitalSysDB = MyApplication.getInstance().getHospitalDB();
    }

    // 自定义更新排班信息列表
    public void updateAppointmentList(List<Schedule> scheduleList) {
        this.mScheduleList = scheduleList;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mScheduleList.size();
    }

    @Override
    public Object getItem(int i) {
        return mScheduleList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return mScheduleList.get(i).getScheduleId();
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.doctorinfo_schedule_item_list, null);
            holder = new ViewHolder();
            holder.tv_date = convertView.findViewById(R.id.tv_date);
            holder.tv_period = convertView.findViewById(R.id.tv_period);
            holder.tv_remain = convertView.findViewById(R.id.tv_remain);
            holder.btn_appoint = convertView.findViewById(R.id.btn_appoint);
            convertView.setTag(holder);
        }
        else {
            holder = (ViewHolder) convertView.getTag();
        }
        Schedule schedule = mScheduleList.get(i);
        // 设置控件取值
        // 排班日期
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String dateStr = sdf.format(schedule.getScheduleDate());;
        holder.tv_date.setText(dateStr);
        // 时段
        holder.tv_period.setText(Schedule.periodStr[schedule.getSchedulePeriod()]);
        // 剩余号数
        new Thread(new Runnable() {
            @Override
            public void run() {
                AppointmentDao appointmentDao = new AppointmentDao();
                long appointed_count = appointmentDao.searchRemainByScheduleId(schedule.getScheduleId());

                // 返回主线程
                ((Activity) mContext).runOnUiThread(()->{
                    holder.tv_remain.setText(String.valueOf(schedule.getAppointmentNum() - appointed_count));

                    // 预约按钮
                    holder.btn_appoint.setOnClickListener(v -> {
                        // 检查预约人数是否已满
                        if (appointed_count >= schedule.getAppointmentNum()) {
                            ToastUtil.show(mContext, "抱歉，预约人数已满，无法成功预约");
                            return;
                        }

                        // 检查当前日期是否在排班日期之前
                        Date currentDate = new Date(); // 获取当前日期
                        if (!currentDate.before(schedule.getScheduleDate())) {
                            ToastUtil.show(mContext, "抱歉，您必须提前一天进行预约");
                            return;
                        }

                        // 检查当前患者是否重复预约该排班
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                Appointment appointment = appointmentDao.searchByScheduleIdAndPatientId(schedule.getScheduleId(), user_name);
                                ((Activity) mContext).runOnUiThread(()->{
                                    if (appointment != null) {
                                        ToastUtil.show(mContext, "您已预约过该班，无法重复预约。");
                                        return;
                                    }

                                    Dialog dialog = new Dialog(mContext);
                                    dialog.setContentView(R.layout.dialog_confirm_appointment_info);
                                    TextView tv_doctor_cfm = dialog.findViewById(R.id.tv_doctor_cfm);
                                    TextView tv_office_cfm = dialog.findViewById(R.id.tv_office_cfm);
                                    TextView tv_visit_date_cfm = dialog.findViewById(R.id.tv_visit_date_cfm);
                                    TextView tv_visit_period_cfm = dialog.findViewById(R.id.tv_visit_period_cfm);
                                    TextView tv_serialNum_cfm = dialog.findViewById(R.id.tv_serialNum_cfm);
                                    TextView tv_price_cfm = dialog.findViewById(R.id.tv_price_cfm);
                                    android.widget.Button btn_back = dialog.findViewById(R.id.btn_back);
                                    android.widget.Button btn_payment = dialog.findViewById(R.id.btn_payment);

                                    // 设置对话框高度和宽度
                                    Window window = dialog.getWindow();
                                    if (window != null) {
                                        WindowManager.LayoutParams params = window.getAttributes();
                                        params.width = (int) (getScreenWidth(mContext) * 0.8);
                                        params.height = (int) (getScreenHeight(mContext) * 0.8);
                                        window.setAttributes(params);
                                    }

                                    // 设置确认预约信息对话框的取值
                                    // 医生姓名
                                    tv_doctor_cfm.setText(doctor_name);
                                    // 查询医生所在科室名称
                                    new Thread(new Runnable() {
                                        @Override
                                        public void run() {
                                            OfficeDao officeDao = new OfficeDao();
                                            Office office = officeDao.searchByDoctorId(doctor_id);

                                            ((Activity) mContext).runOnUiThread(()->{
                                                tv_office_cfm.setText(office.getName());
                                            });
                                        }
                                    }).start();

                                    // 就诊日期
                                    tv_visit_date_cfm.setText(sdf.format(schedule.getScheduleDate()));
                                    // 就诊时段
                                    tv_visit_period_cfm.setText(Schedule.periodStr[schedule.getSchedulePeriod()]);
                                    // 就诊序号
                                    tv_serialNum_cfm.setText(String.valueOf(appointed_count + 1));
                                    // 价格
                                    tv_price_cfm.setText(String.valueOf(mPrice));     // 价格始终设置为20

                                    // 对话框返回按钮监听
                                    btn_back.setOnClickListener(v1 -> {
                                        ToastUtil.show(mContext, "返回成功");
                                        dialog.cancel();
                                    });

                                    // 对话框支付按钮监听
                                    btn_payment.setOnClickListener(v2 -> {
                                        ToastUtil.show(mContext, "支付成功!");
                                        Appointment newAppointment = new Appointment();
                                        // 设置排班记录id
                                        newAppointment.setScheduleId(schedule.getScheduleId());
                                        // 设置医生id
                                        newAppointment.setDoctorId(doctor_id);
                                        // 设置患者id
                                        newAppointment.setPatientId(user_name);
                                        // 设置就诊时间
                                        newAppointment.setVisitDate(schedule.getScheduleDate());
                                        // 预约时间设置为当前时刻时间
                                        newAppointment.setOrderTime(new Date(System.currentTimeMillis()));
                                        // 设置预约序号
                                        newAppointment.setSerialNum((int) appointed_count + 1);
                                        // 设置预约状态，待处理
                                        newAppointment.setOrderState(Appointment.WAIT);
                                        // 设置预约时段
                                        newAppointment.setVisitPeriod(schedule.getSchedulePeriod());
                                        new Thread(new Runnable() {
                                            @Override
                                            public void run() {
                                                long appointment_id = appointmentDao.insert(newAppointment);

                                                // 插入支付记录
                                                Payment payment = new Payment();
                                                payment.setPayerId(user_name);
                                                payment.setAppointmentId((int) appointment_id);
                                                payment.setPaymentAmount(mPrice);
                                                payment.setPayTime(new Date(System.currentTimeMillis()));

                                                PaymentDao paymentDao = new PaymentDao();
                                                long success = paymentDao.insert(payment);

                                                if (success > 0) {
                                                    // 更新 UI
                                                    ((Activity) mContext).runOnUiThread(() -> {
                                                        ToastUtil.show(mContext, "已成功预约!");
                                                        dialog.cancel();
                                                        // 让adapter感知到变化
                                                        notifyDataSetChanged();
                                                    });
                                                }
                                                else {
                                                    // 更新 UI
                                                    ((Activity) mContext).runOnUiThread(() -> {
                                                        ToastUtil.show(mContext, "支付失败");
                                                        dialog.cancel();
                                                        notifyDataSetChanged();
                                                    });
                                                }
                                            }
                                        }).start();
                                    });
                                    dialog.show();
                                    ToastUtil.show(mContext, "请您在核对预约信息无误后，再进行支付操作");
                                });
                            }
                        }).start();
                    });
                });
            }
        }).start();
        return convertView;
    }

    public final class ViewHolder {
        public TextView tv_period;
        public TextView tv_date;
        public TextView tv_remain;
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
