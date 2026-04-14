package com.example.hospitalsystem.Service.Impl;

import com.example.hospitalsystem.Entity.Doctor;
import com.example.hospitalsystem.Repository.DoctorRepository;
import com.example.hospitalsystem.Service.DoctorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DoctorServiceImpl implements DoctorService {
    @Autowired
    private DoctorRepository doctorRepository;

    @Override
    public Long count() {
        return doctorRepository.count();
    }

    @Override
    public void delete(Doctor doctor) {
        doctorRepository.delete(doctor);
    }

    @Override
    public void deleteAll() {
        doctorRepository.deleteAll();
    }

    @Override
    public void deleteAll(List<Doctor> doctors) {
        doctorRepository.deleteAll(doctors);
    }

    @Override
    public void deleteAllById(List<String> ids) {
        doctorRepository.deleteAllById(ids);
    }

    @Override
    public void deleteById(String id) {
        doctorRepository.deleteById(id);
    }

    @Override
    public boolean existsById(String id) {
        return doctorRepository.existsById(id);
    }

    @Override
    public List<Doctor> findAll() {
        return doctorRepository.findAll();
    }

    @Override
    public List<Doctor> findAllById(List<String> ids) {
        return doctorRepository.findAllById(ids);
    }

    @Override
    public Doctor findById(String id) {
        return doctorRepository.findById(id).orElse(null);
    }

    @Override
    public Doctor save(Doctor doctor) {
        return doctorRepository.save(doctor);
    }

    @Override
    public List<Doctor> saveAll(List<Doctor> doctors) {
        return doctorRepository.saveAll(doctors);
    }
}
