package com.example.hospitalsystem.Service;

import com.example.hospitalsystem.Entity.Doctor;
import com.example.hospitalsystem.Entity.DoctorInfo;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DoctorInfoService {
    Long count();

    void delete(DoctorInfo doctorInfo);

    void deleteAll();

    void deleteAll(List<DoctorInfo> doctorInfos);

    void deleteAllById(List<Long> ids);

    void deleteById(Long id);

    boolean existsById(Long id);

    List<DoctorInfo> findAll();

    List<DoctorInfo> findAllById(List<Long> ids);

    DoctorInfo findById(Long id);

    DoctorInfo save(DoctorInfo doctorInfo);

    List<DoctorInfo> saveAll(List<DoctorInfo> doctorInfos);

    // 根据医生用户名查询医生信息
    DoctorInfo findByDoctorId(String doctor_id);

    // 根据科室id删除医生信息
    void deleteByOfficeId(Long office_id);

}
