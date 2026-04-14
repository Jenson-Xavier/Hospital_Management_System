package com.example.hospitalsystem.Repository;

import com.example.hospitalsystem.Entity.Office;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface OfficeRepository extends JpaRepository<Office, Long> {

    // 根据科室名称部分匹配查询
    @Query(value = "select * from Office where name like %:name%", nativeQuery = true)
    List<Office> findByPartialName(@Param("name") String name);

    // 根据医生用户名进行查询
    Office findByName(String name);

    // 根据id删除
    @Modifying
    @Query(value = "delete from Office o where o.office_id = :id")
    void deleteByOfficeId(@Param("id") Long id);

}
