package com.example.hospitalsystem.Service;

import com.example.hospitalsystem.Entity.Patient;
import jakarta.transaction.Transactional;

import java.util.List;

public interface PatientService {
    Long count();

    void delete(Patient patient);

    void deleteAll();

    void deleteAll(List<Patient> patients);

    void deleteAllById(List<String> ids);

    void deleteById(String id);

    boolean existsById(String id);

    List<Patient> findAll();

    List<Patient> findAllById(List<String> ids);

    Patient findById(String id);

    Patient save(Patient patient);

    List<Patient> saveAll(List<Patient> patients);

    // 删除患者用户 (需级联删除User和Patient)
    void deletePatientById(String user_id);
}
