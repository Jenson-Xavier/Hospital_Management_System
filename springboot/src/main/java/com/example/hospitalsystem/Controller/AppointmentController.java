package com.example.hospitalsystem.Controller;

import com.example.hospitalsystem.Entity.Appointment;
import com.example.hospitalsystem.Entity.Doctor;
import com.example.hospitalsystem.Entity.Patient;
import com.example.hospitalsystem.Entity.Schedule;
import com.example.hospitalsystem.Service.AppointmentService;
import com.example.hospitalsystem.Service.DoctorService;
import com.example.hospitalsystem.Service.PatientService;
import com.example.hospitalsystem.Service.ScheduleService;
import com.example.hospitalsystem.pojo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/appointment")
public class AppointmentController {

    @Autowired
    private AppointmentService appointmentService;

    @Autowired
    private PatientService patientService;

    @Autowired
    private DoctorService doctorService;

    @Autowired
    private ScheduleService scheduleService;

    @PostMapping("/add")
    public Result addAppointment(
            @RequestParam Map<String, Object> params
    ) {
        System.out.println(params);
        // 提取参数
        String appointment_idStr = (String) params.get("appointment_id");
        int appointment_id = Integer.parseInt(appointment_idStr);
        String patient_id = (String) params.get("patient_id");
        String doctor_id = (String) params.get("doctor_id");
        String schedule_idStr = (String) params.get("schedule_id");
        long schedule_id = Long.parseLong(schedule_idStr);

        String visit_date = (String) params.get("visit_date");
        String visit_periodStr = (String) params.get("visit_period");
        int visit_period = Integer.parseInt(visit_periodStr);
        String serial_numStr = (String) params.get("serial_num");
        int serial_num = Integer.parseInt(serial_numStr);
        String order_stateStr = (String) params.get("order_state");
        int order_state = Integer.parseInt(order_stateStr);
        String mode = (String) params.get("mode");

        System.out.println(mode);

        // 解析日期
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;
        try {
            date = formatter.parse(visit_date);
        } catch (ParseException e) {
            e.printStackTrace();
            return Result.error("日期格式解析错误");
        }

        // 查询医生
        Doctor doctor = doctorService.findById(doctor_id);
        // 查询患者
        Patient patient = patientService.findById(patient_id);
        // 查询排班信息
        Schedule schedule = scheduleService.findById(schedule_id);


        Appointment appointment = new Appointment();
        appointment.setPatient(patient);
        appointment.setDoctor(doctor);
        appointment.setSchedule(schedule);
        appointment.setVisit_period(visit_period);
        appointment.setSerial_num(serial_num);
        appointment.setOrder_state(order_state);
        appointment.setVisit_date(date);
        System.out.println(params);

        if (!StringUtils.hasLength((String) params.get("order_time"))) {
            appointment.setOrder_time(new Timestamp(System.currentTimeMillis()));
        }
        else {
            System.out.println(params);
            appointment.setOrder_time(Timestamp.valueOf((String) params.get("order_time")));
        }

        if (mode.equals("update")) {
            appointment.setAppointment_id(appointment_id);
        }
        // 添加预约记录
        appointmentService.save(appointment);
        return Result.success();
    }

    @GetMapping("/getAll")
    public Result<List<Appointment>> getAllAppointment() {
        List<Appointment> appointments = appointmentService.findAll();
        return Result.success(appointments);
    }

    @PostMapping("/delete")
    public Result deleteAppointment(String id) {
        long appointment_id = Long.parseLong(id);
        Appointment appointment = appointmentService.findById(appointment_id);
        if (appointment == null) {
            return Result.error("不存在该预约记录，无法删除");
        }
        appointmentService.delete(appointment);
        return Result.success();
    }

    // 处理预约请求
    @PostMapping("/solve")
    public Result solveAppointment(String id) {
        long appointment_id = Long.parseLong(id);
        Appointment appointment = appointmentService.findById(appointment_id);
        if (appointment == null) {
            return Result.error("不存在该预约记录，无法删除");
        }
        appointment.setOrder_state(Appointment.DONE);
        appointmentService.save(appointment);
        return Result.success();
    }

}
