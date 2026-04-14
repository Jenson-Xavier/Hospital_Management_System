package com.example.hospitalsystem.Repository;

import com.example.hospitalsystem.Entity.DoctorInfo;
import com.example.hospitalsystem.Entity.Office;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface DoctorInfoRepository extends JpaRepository<DoctorInfo, Long> {

    // 根据医生姓名查询 (部分匹配)
    @Query(value = "select * from doctor_infos where name like '%' || :name || '%'", nativeQuery = true)
    List<DoctorInfo> findByName(String name);

    // 根据医生工号查询
    @Query(value = "select di from doctor_infos di where di.doctor.user_id = :doctor_id")
    DoctorInfo findByDoctorId(@Param("doctor_id") String doctor_id);

    // 根据id删除
    @Modifying
    @Query(value = "delete from doctor_infos di where di.doctor_info_id = :id")
    void deleteByDoctorInfoId(@Param("id") Long id);

    // 根据科室id删除医生信息
    @Modifying
    @Query(value = "delete from doctor_infos di where di.office.office_id = :office_id")
    void deleteByOfficeId(@Param("office_id") Long office_id);

}
