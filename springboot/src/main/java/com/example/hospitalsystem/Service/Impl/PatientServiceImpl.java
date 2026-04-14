package com.example.hospitalsystem.Service.Impl;

import com.example.hospitalsystem.Entity.Patient;
import com.example.hospitalsystem.Repository.PatientRepository;
import com.example.hospitalsystem.Repository.UserRepository;
import com.example.hospitalsystem.Service.PatientService;
import com.example.hospitalsystem.utils.CodeUtil;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class PatientServiceImpl implements PatientService {
    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    public Long count() {
        return patientRepository.count();
    }

    @Override
    public void delete(Patient patient) {
        patientRepository.delete(patient);
    }

    @Override
    public void deleteAll() {
        patientRepository.deleteAll();
    }

    @Override
    public void deleteAll(List<Patient> patients) {
        patientRepository.deleteAll(patients);
    }

    @Override
    public void deleteAllById(List<String> ids) {
        patientRepository.deleteAllById(ids);
    }

    @Override
    public void deleteById(String id) {
        patientRepository.deleteById(id);
    }

    @Override
    public boolean existsById(String id) {
        return patientRepository.existsById(id);
    }

    @Override
    public List<Patient> findAll() {
        return patientRepository.findAll();
    }

    @Override
    public List<Patient> findAllById(List<String> ids) {
        return patientRepository.findAllById(ids);
    }

    @Override
    public Patient findById(String id) {
        return patientRepository.findById(id).orElse(null);
    }

    @Override
    public Patient save(Patient patient) {
        return patientRepository.save(patient);
    }

    @Override
    public List<Patient> saveAll(List<Patient> patients) {
        return patientRepository.saveAll(patients);
    }

    @Override
    @Transactional
    public void deletePatientById(String user_id) {
        patientRepository.deleteById(user_id); // 先删除子类实体
        userRepository.deleteById(user_id);    // 然后删除父类实体
    }

}
