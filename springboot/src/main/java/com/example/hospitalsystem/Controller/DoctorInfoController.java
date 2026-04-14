package com.example.hospitalsystem.Controller;

import com.example.hospitalsystem.Entity.Doctor;
import com.example.hospitalsystem.Entity.DoctorInfo;
import com.example.hospitalsystem.Entity.Office;
import com.example.hospitalsystem.Service.DoctorInfoService;
import com.example.hospitalsystem.Service.DoctorService;
import com.example.hospitalsystem.Service.OfficeService;
import com.example.hospitalsystem.pojo.Result;
import com.example.hospitalsystem.utils.ThreadLocalUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.print.Doc;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/doctorInfo")
public class DoctorInfoController {

    @Autowired
    private DoctorInfoService doctorInfoService;

    @Autowired
    private DoctorService doctorService;

    @Autowired
    private OfficeService officeService;

    // 获取指定同户名的医生信息
    @GetMapping("/get")
    public Result<DoctorInfo> getDoctorInfo() {
        Map<String, Object> map = ThreadLocalUtil.get();
        String username = (String) map.get("username");
        DoctorInfo doctorInfo = doctorInfoService.findByDoctorId(username);
        return Result.success(doctorInfo);
    }

    // 获取全部医生信息
    @GetMapping("/getAll")
    public Result<List<DoctorInfo>> getDoctorInfoList() {
        List<DoctorInfo> doctorInfoList = doctorInfoService.findAll();
        return Result.success(doctorInfoList);
    }

    // 更新医生信息
    @PostMapping("/update")
    public Result updateDoctorInfo(@RequestParam Map<String, String> params) {
        System.out.println(params);
        // 提取参数
        long office_id = Long.parseLong(params.get("office"));
        int gender = Integer.parseInt(params.get("gender"));
        int seniority = Integer.parseInt(params.get("seniority"));
        String doctor_id = params.get("doctor_id");
        String name = params.get("doctor_name");
        String intro = params.get("intro");

        // 根据医生工号查询医生信息，据此判断医生信息是否存在
        DoctorInfo doctorInfo = doctorInfoService.findByDoctorId(doctor_id);
        // 不存在医生信息，则现在为新增
        if (doctorInfo == null) {
            doctorInfo = new DoctorInfo();
        }

        // 查询医生
        Doctor doctor = doctorService.findById(doctor_id);
        if (doctor == null) {
            return Result.error("该医生用户不存在");
        }
        // 查询科室
        Office office = officeService.findById(office_id);
        if (office == null) {
            return Result.error("该科室不存在");
        }

        doctorInfo.setDoctor(doctor);
        doctorInfo.setName(name);
        doctorInfo.setIntro(intro);
        doctorInfo.setGender(gender);
        doctorInfo.setSeniority(seniority);
        doctorInfo.setOffice(office);
        // 添加/修改doctorInfo
        doctorInfoService.save(doctorInfo);
        return Result.success();
    }

    // 删除医生信息
    @PostMapping("/del")
    public Result delDoctorInfo(String id) {
        long doctor_info_id = Long.parseLong(id);
        DoctorInfo doctorInfo = doctorInfoService.findById(doctor_info_id);
        if (doctorInfo == null) {
            return Result.error("该医生信息不存在，无法删除");
        }
        // 内部是自定义的sql语言删除写法，jpa内置接口无法解决OneToOne的级联删除问题
        doctorInfoService.deleteById(doctor_info_id);
        return Result.success();
    }

    // 根据科室id删除医生信息
    @PostMapping("/delByOfficeId")
    public Result delDoctorInfoByOfficeId(String id) {
        long office_id = Long.parseLong(id);
        doctorInfoService.deleteByOfficeId(office_id);
        return Result.success();
    }

}
