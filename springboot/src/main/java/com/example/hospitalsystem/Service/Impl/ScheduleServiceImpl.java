package com.example.hospitalsystem.Service.Impl;

import com.example.hospitalsystem.Entity.Schedule;
import com.example.hospitalsystem.Repository.ScheduleRepository;
import com.example.hospitalsystem.Service.ScheduleService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ScheduleServiceImpl implements ScheduleService {
    @Autowired
    private ScheduleRepository scheduleRepository;

    @Override
    public Long count() {
        return scheduleRepository.count();
    }

    @Override
    public void delete(Schedule schedule) {
        scheduleRepository.delete(schedule);
    }

    @Override
    public void deleteAll() {
        scheduleRepository.deleteAll();
    }

    @Override
    public void deleteAll(List<Schedule> schedules) {
        scheduleRepository.deleteAll(schedules);
    }

    @Override
    public void deleteAllById(List<Long> ids) {
        scheduleRepository.deleteAllById(ids);
    }

    // 需要手动写
    @Transactional
    @Override
    public void deleteById(Long id) {
        scheduleRepository.deleteScheduleById(id);
    }

    @Override
    public boolean existsById(Long id) {
        return scheduleRepository.existsById(id);
    }

    @Override
    public List<Schedule> findAll() {
        return scheduleRepository.findAll();
    }

    @Override
    public List<Schedule> findAllById(List<Long> ids) {
        return scheduleRepository.findAllById(ids);
    }

    @Override
    public Schedule findById(Long id) {
        return scheduleRepository.findById(id).orElse(null);
    }

    @Override
    public Schedule save(Schedule schedule) {
        return scheduleRepository.save(schedule);
    }

    @Override
    public List<Schedule> saveAll(List<Schedule> schedules) {
        return scheduleRepository.saveAll(schedules);
    }

    @Override
    @Transactional
    public void deleteByOfficeId(Long officeId) {
        scheduleRepository.deleteByOfficeId(officeId);
    }
}
