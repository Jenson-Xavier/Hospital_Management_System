package com.example.hospitalsystem.Service;

import com.example.hospitalsystem.Entity.Doctor;

import java.util.List;

public interface DoctorService {
    Long count();

    void delete(Doctor doctor);

    void deleteAll();

    void deleteAll(List<Doctor> doctors);

    void deleteAllById(List<String> ids);

    void deleteById(String id);

    boolean existsById(String id);

    List<Doctor> findAll();

    List<Doctor> findAllById(List<String> ids);

    Doctor findById(String id);

    Doctor save(Doctor doctor);

    List<Doctor> saveAll(List<Doctor> doctors);
}
