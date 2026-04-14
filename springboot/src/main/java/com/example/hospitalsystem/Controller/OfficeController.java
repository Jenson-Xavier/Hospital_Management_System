package com.example.hospitalsystem.Controller;

import com.example.hospitalsystem.Entity.DoctorInfo;
import com.example.hospitalsystem.Entity.Office;
import com.example.hospitalsystem.Entity.Schedule;
import com.example.hospitalsystem.Service.DoctorInfoService;
import com.example.hospitalsystem.Service.OfficeService;
import com.example.hospitalsystem.Service.ScheduleService;
import com.example.hospitalsystem.pojo.Result;
import com.example.hospitalsystem.utils.ThreadLocalUtil;
import jakarta.transaction.Transactional;
import org.ietf.jgss.Oid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/office")
public class OfficeController {

    @Autowired
    private OfficeService officeService;

    @Autowired
    private DoctorInfoService doctorInfoService;

    @Autowired
    private ScheduleService scheduleService;

    // 查询全部科室
    @GetMapping("/getAll")
    public Result<List<Office>> getOfficeList() {
        List<Office> officeList = officeService.findAll();
        return Result.success(officeList);
    }

    // 查询指定医生用户的科室
    @PostMapping("/getById")
    public Result<Office> getOfficeById(String id) {
        long officeId = Long.parseLong(id);
        Office office = officeService.findById(officeId);
        return Result.success(office);
    }

    // 修改科室信息
    @PostMapping("/update")
    public Result updateOffice(@RequestParam Map<String, String> params) {
        // 提取参数
        String name = params.get("name");
        String intro = params.get("intro");
        String mode = params.get("mode");

        Office office = new Office();
        if (mode.equals("update")) {
            long office_id = Long.parseLong(params.get("office_id"));
            office = officeService.findById(office_id);
            if (office == null) {
                return Result.error("该科室信息不存在");
            }
        }
        office.setName(name);
        office.setIntro(intro);
        officeService.save(office);
        return Result.success(office);
    }

    // 删除科室
    @PostMapping("/delete")
    @Transactional
    public Result deleteOffice(String id) {
        long officeId = Long.parseLong(id);
        Office office = officeService.findById(officeId);
        if (office == null) {
            return Result.error("科室信息不存在，无法删除");
        }
        // 首先删除科室对应的医生信息
        doctorInfoService.deleteByOfficeId(officeId);
        officeService.deleteById(officeId);
        return Result.success();
    }
}
