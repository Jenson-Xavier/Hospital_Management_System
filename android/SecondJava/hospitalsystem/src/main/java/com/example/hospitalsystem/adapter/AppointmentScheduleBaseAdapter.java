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
import com.example.hospitalsystem.db_Dao.DoctorInfoDao;
import com.example.hospitalsystem.db_Dao.PaymentDao;
import com.example.hospitalsystem.db_Dao.ScheduleDao;
import com.example.hospitalsystem.db_Entity.Appointment;
import com.example.hospitalsystem.db_Entity.DoctorInfo;
import com.example.hospitalsystem.db_Entity.Payment;
import com.example.hospitalsystem.db_Entity.Schedule;
import com.example.hospitalsystem.util.ToastUtil;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class AppointmentScheduleBaseAdapter extends BaseAdapter {

    private final HospitalSysDatabase hospitalSysDB;
    private List<Schedule> mSheduleList;
    private Context mContext;
    private String user_name;
    private String office_name;
    private int office_id;
    private int mPrice = 20;

    public AppointmentScheduleBaseAdapter(List<Schedule> mSheduleList, String user_name, String office_name, int office_id, Context mContext) {
        this.mSheduleList = mSheduleList;
        this.mContext = mContext;
        this.user_name = user_name;
        this.office_name = office_name;
        this.office_id = office_id;
        hospitalSysDB = MyApplication.getInstance().getHospitalDB();
    }


    // 自定义更新科室信息列表
    public void updateOfficeList(List<Schedule> scheduleList) {
        this.mSheduleList = scheduleList;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mSheduleList.size();
    }

    @Override
    public Object getItem(int i) {
        return mSheduleList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return mSheduleList.get(i).getScheduleId();
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.schedule_item_list, null);
            holder = new ViewHolder();
            holder.tv_doctor_name = convertView.findViewById(R.id.tv_doctor_name);
            holder.tv_period = convertView.findViewById(R.id.tv_period);
            holder.tv_remain = convertView.findViewById(R.id.tv_remain);
            holder.tv_date = convertView.findViewById(R.id.tv_date);
            holder.btn_doctor_intro = convertView.findViewById(R.id.btn_doctor_intro);
            holder.btn_appoint = convertView.findViewById(R.id.btn_appoint);
            convertView.setTag(holder);
        }
        else {
            holder = (ViewHolder) convertView.getTag();
        }
        Schedule schedule = mSheduleList.get(i);

        // 排班时段
        int schedulePeriod = schedule.getSchedulePeriod();
        holder.tv_period.setText(Schedule.periodStr[schedulePeriod]);

        // 排班日期 首先将日期从Date类型转化为String类型
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String dateStr = sdf.format(schedule.getScheduleDate());;
        holder.tv_date.setText(dateStr);

        new Thread(new Runnable() {
            @Override
            public void run() {
                DoctorInfoDao doctorInfoDao = new DoctorInfoDao();
                // 根据医生id查询医生信息
                DoctorInfo doctorInfo = doctorInfoDao.searchByDoctorId(schedule.getDoctorId());

                // 查询号量余额 (schedule_id, 预约状态不是取消)
                AppointmentDao appointmentDao = new AppointmentDao();
                long appointed_count = appointmentDao.searchRemainByScheduleId(schedule.getScheduleId());

                // 返回主线程
                ((Activity) mContext).runOnUiThread(()->{
                    // 更新医生姓名
                    if (doctorInfo != null) {
                        holder.tv_doctor_name.setText(doctorInfo.getName());
                    }

                    // 更新号量余额
                    if (appointed_count >= 0) {
                        holder.tv_remain.setText(String.valueOf(schedule.getAppointmentNum() - appointed_count));
                    }

                    // 医生介绍按钮
                    holder.btn_doctor_intro.setOnClickListener(v -> {
                        Dialog dialog = new Dialog(mContext);
                        dialog.setContentView(R.layout.dialog_doctor_intro);
                        TextView tv_doctor_id = dialog.findViewById(R.id.tv_doctor_id);
                        TextView tv_name = dialog.findViewById(R.id.tv_name);
                        TextView tv_gender = dialog.findViewById(R.id.tv_gender);
                        TextView tv_seniority = dialog.findViewById(R.id.tv_seniority);
                        TextView tv_intro = dialog.findViewById(R.id.tv_intro);

                        // 设置对话框的信息
                        if (doctorInfo != null) {
                            tv_doctor_id.setText(doctorInfo.getDoctorId());
                            tv_name.setText(doctorInfo.getName());
                            tv_gender.setText(doctorInfo.getGender() == DoctorInfo.MALE ? "男" : "女");
                            tv_seniority.setText(String.valueOf(doctorInfo.getSeniority()));
                            tv_intro.setText(doctorInfo.getIntro());
                        }

                        // 设置对话框宽度为屏幕宽度的80%
                        Window window = dialog.getWindow();
                        if (window != null) {
                            WindowManager.LayoutParams params = window.getAttributes();
                            params.width = (int) (getScreenWidth(mContext) * 0.8);
                            params.height = (int) (getScreenHeight(mContext) * 0.6);
                            window.setAttributes(params);
                        }
                        dialog.show();
                        ToastUtil.show(mContext, "这是关于医生" +  (doctorInfo != null ? doctorInfo.getName() : "") + "的介绍");
                    });

                    // 预约按钮监听
                    holder.btn_appoint.setOnClickListener(v -> {
                        // 检查预约人数是否已满
                        if (appointed_count == schedule.getAppointmentNum()) {
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

                                // 返回主线程
                                ((Activity) mContext).runOnUiThread(()->{
                                    if (appointment != null) {
                                        ToastUtil.show(mContext, "您已预约过该班，无法重复预约。");
                                        return;
                                    }

                                    // 显示确认预约的对话框
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
                                    if (doctorInfo != null) {
                                        tv_doctor_cfm.setText(doctorInfo.getName());
                                    }
                                    tv_office_cfm.setText(office_name);
                                    tv_visit_date_cfm.setText(sdf.format(schedule.getScheduleDate()));
                                    tv_visit_period_cfm.setText(Schedule.periodStr[schedule.getSchedulePeriod()]);
                                    tv_serialNum_cfm.setText(String.valueOf(appointed_count + 1));
                                    tv_price_cfm.setText(String.valueOf(mPrice));     // 价格始终设置为20

                                    // 对话框返回按钮监听
                                    btn_back.setOnClickListener(v1 -> {
                                        ToastUtil.show(mContext, "返回成功");
                                        dialog.cancel();
                                    });

                                    // 对话框支付按钮监听
                                    btn_payment.setOnClickListener(v2 -> {
                                        ToastUtil.show(mContext, "支付成功!");
                                        new Thread(new Runnable() {
                                            @Override
                                            public void run() {
                                                Appointment newAppointment = new Appointment();
                                                // 设置排班记录id
                                                newAppointment.setScheduleId(schedule.getScheduleId());
                                                // 设置医生id
                                                if (doctorInfo != null) {
                                                    newAppointment.setDoctorId(doctorInfo.getDoctorId());
                                                }
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
                                                // 执行插入操作
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
        public TextView tv_doctor_name;
        public TextView tv_period;
        public TextView tv_remain;
        public TextView tv_date;
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
