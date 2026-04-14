package com.example.hospitalsystem.Repository;

import com.example.hospitalsystem.Entity.Office;
import com.example.hospitalsystem.Entity.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {

    // 根据id删除排班信息
    @Modifying
    @Query(value = "delete from Schedule sd where sd.schedule_id = :id")
    void deleteScheduleById(@Param("id") Long id);

    // 根据科室id删除排班信息
    @Modifying
    @Query(value = "delete from Schedule sd where sd.office.office_id = :office_id")
    void deleteByOfficeId(@Param("office_id") Long office_id);
}
