package com.example.hospitalsystem.Controller;

import com.example.hospitalsystem.Entity.Doctor;
import com.example.hospitalsystem.Entity.Office;
import com.example.hospitalsystem.Entity.Schedule;
import com.example.hospitalsystem.Service.DoctorService;
import com.example.hospitalsystem.Service.OfficeService;
import com.example.hospitalsystem.Service.ScheduleService;
import com.example.hospitalsystem.pojo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.*;


import java.sql.Date;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/schedule")
public class ScheduleController {

    @Autowired
    private ScheduleService scheduleService;

    @Autowired
    private DoctorService doctorService;

    @Autowired
    private OfficeService officeService;

    // 查询所有排班信息
    @GetMapping("/getAll")
    public Result<List<Schedule>> getScheduleList() {
        List<Schedule> scheduleList = scheduleService.findAll();
        return Result.success(scheduleList);
    }

    // 添加排班信息
    @PostMapping("/add")
    public Result addSchedule(@RequestParam Map<String, String> map) {
        // 提取参数信息
        System.out.println(map);
        long office_id = Long.parseLong(map.get("office_id"));
        String doctor_id = map.get("doctor_id");
        int schedule_period = Integer.parseInt(map.get("schedule_period"));
        int appointment_num = Integer.parseInt(map.get("appointment_num"));
        Date schedule_date = Date.valueOf(map.get("schedule_date"));
        String mode = map.get("mode");

        long schedule_id = Long.parseLong(map.get("schedule_id"));
        Schedule schedule = new Schedule();
        if (mode.equals("update")) {
            schedule = scheduleService.findById(schedule_id);
            if (schedule == null) {
                return Result.error("该排班信息不存在，无法删除");
            }
        }
        // 查医生
        Doctor doctor = doctorService.findById(doctor_id);
        if (doctor == null) {
            return Result.error("该医生用户不存在");
        }
        // 查科室
        Office office = officeService.findById(office_id);
        if (office == null) {
            return Result.error("该科室不存在");
        }
        schedule.setSchedule_date(schedule_date);
        schedule.setSchedule_period(schedule_period);
        schedule.setAppointment_num(appointment_num);
        schedule.setDoctor(doctor);
        schedule.setOffice(office);
        scheduleService.save(schedule);
        return Result.success();
    }

    // 删除排班信息
    @PostMapping("/delete")
    public Result deleteSchedule(String id) {
        long schedule_id = Long.parseLong(id);
        Schedule schedule = scheduleService.findById(schedule_id);
        if (schedule == null) {
            return Result.error("该排班信息不存在，无法删除");
        }
        scheduleService.deleteById(schedule_id);
        return Result.success();
    }

    // 根据科室Id删除排班信息
    @PostMapping("/delByOfficeId")
    public Result delByOfficeId(String id) {
        long office_id = Long.parseLong(id);
        scheduleService.deleteByOfficeId(office_id);
        return Result.success();
    }


}
