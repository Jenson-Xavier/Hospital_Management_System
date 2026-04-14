package com.example.hospitalsystem.Service.Impl;

import com.example.hospitalsystem.Entity.DoctorInfo;
import com.example.hospitalsystem.Repository.DoctorInfoRepository;
import com.example.hospitalsystem.Service.DoctorInfoService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DoctorInfoServiceImpl implements DoctorInfoService {

    @Autowired
    private DoctorInfoRepository doctorInfoRepository;

    @Override
    public Long count() {
        return doctorInfoRepository.count();
    }

    @Override
    public void delete(DoctorInfo doctorInfo) {
        doctorInfoRepository.delete(doctorInfo);
    }

    @Override
    public void deleteAll() {
        doctorInfoRepository.deleteAll();
    }

    @Override
    public void deleteAll(List<DoctorInfo> doctorInfos) {
        doctorInfoRepository.deleteAll(doctorInfos);
    }

    @Override
    public void deleteAllById(List<Long> ids) {
        doctorInfoRepository.deleteAllById(ids);
    }

    // 需要手动写
    @Override
    @Transactional
    public void deleteById(Long id) {
        doctorInfoRepository.deleteByDoctorInfoId(id);
    }

    @Override
    public boolean existsById(Long id) {
        return doctorInfoRepository.existsById(id);
    }

    @Override
    public List<DoctorInfo> findAll() {
        return doctorInfoRepository.findAll();
    }

    @Override
    public List<DoctorInfo> findAllById(List<Long> ids) {
        return doctorInfoRepository.findAllById(ids);
    }

    @Override
    public DoctorInfo findById(Long id) {
        return doctorInfoRepository.findById(id).orElse(null);
    }

    @Override
    public DoctorInfo save(DoctorInfo doctorInfo) {
        return doctorInfoRepository.save(doctorInfo);
    }

    @Override
    public List<DoctorInfo> saveAll(List<DoctorInfo> doctorInfos) {
        return doctorInfoRepository.saveAll(doctorInfos);
    }

    // 根据医生用户名查询医生信息
    @Override
    public DoctorInfo findByDoctorId(String doctor_id) {
        return doctorInfoRepository.findByDoctorId(doctor_id);
    }

    @Override
    @Transactional
    public void deleteByOfficeId(Long office_id) {
        doctorInfoRepository.deleteByOfficeId(office_id);
    }
}
