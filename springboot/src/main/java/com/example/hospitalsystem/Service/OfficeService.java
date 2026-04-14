package com.example.hospitalsystem.Service;

import com.example.hospitalsystem.Entity.Office;

import java.util.List;

public interface OfficeService {

    Long count();

    void delete(Office office);

    void deleteAll();

    void deleteAll(List<Office> offices);

    void deleteAllById(List<Long> ids);

    void deleteById(Long id);

    boolean existsById(Long id);

    List<Office> findAll();

    List<Office> findAllById(List<Long> ids);

    Office findById(Long id);

    Office save(Office office);

    List<Office> saveAll(List<Office> offices);

    Office findByName(String name);

}
