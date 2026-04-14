package com.example.hospitalsystem.Service.Impl;

import com.example.hospitalsystem.Entity.DoctorInfo;
import com.example.hospitalsystem.Entity.Office;
import com.example.hospitalsystem.Entity.Schedule;
import com.example.hospitalsystem.Repository.DoctorInfoRepository;
import com.example.hospitalsystem.Repository.OfficeRepository;
import com.example.hospitalsystem.Repository.ScheduleRepository;
import com.example.hospitalsystem.Service.OfficeService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OfficeServiceImpl implements OfficeService {
    @Autowired
    private OfficeRepository officeRepository;

    @Autowired
    private DoctorInfoRepository doctorInfoRepository;

    @Autowired
    private ScheduleRepository scheduleRepository;

    @Override
    public Long count() {
        return officeRepository.count();
    }

    @Override
    public void delete(Office office) {
        officeRepository.delete(office);
    }

    @Override
    public void deleteAll() {
        officeRepository.deleteAll();
    }

    @Override
    public void deleteAll(List<Office> offices) {
        officeRepository.deleteAll(offices);
    }

    @Override
    public void deleteAllById(List<Long> ids) {
        officeRepository.deleteAllById(ids);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
//        officeRepository.deleteById(id);
        officeRepository.deleteById(id);
    }

//    @Transactional
//    public void deleteById(Long officeId) {
//        Office office = officeRepository.findById(officeId)
//                .orElseThrow(() -> new EntityNotFoundException("Office not found"));
//
//        // Delete related DoctorInfo and Schedule entities
//        doctorInfoRepository.deleteByOffice(office);
//        scheduleRepository.deleteByOffice(office);
//
//        // Now delete the Office
//        officeRepository.delete(office);
//    }

    @Override
    public boolean existsById(Long id) {
        return officeRepository.existsById(id);
    }

    @Override
    public List<Office> findAll() {
        return officeRepository.findAll();
    }

    @Override
    public List<Office> findAllById(List<Long> ids) {
        return officeRepository.findAllById(ids);
    }

    @Override
    public Office findById(Long id) {
        return officeRepository.findById(id).orElse(null);
    }

    @Override
    public Office save(Office office) {
        return officeRepository.save(office);
    }

    @Override
    public List<Office> saveAll(List<Office> offices) {
        return officeRepository.saveAll(offices);
    }

    @Override
    public Office findByName(String name) {
        return officeRepository.findByName(name);
    }
}
