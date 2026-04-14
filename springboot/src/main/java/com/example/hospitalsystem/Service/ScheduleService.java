package com.example.hospitalsystem.Service;

import com.example.hospitalsystem.Entity.Schedule;

import java.util.List;

public interface ScheduleService {
    Long count();

    void delete(Schedule schedule);

    void deleteAll();

    void deleteAll(List<Schedule> schedules);

    void deleteAllById(List<Long> ids);

    void deleteById(Long id);

    boolean existsById(Long id);

    List<Schedule> findAll();

    List<Schedule> findAllById(List<Long> ids);

    Schedule findById(Long id);

    Schedule save(Schedule schedule);

    List<Schedule> saveAll(List<Schedule> schedules);

    // 根据科室id删除排班信息
    void deleteByOfficeId(Long officeId);
}
