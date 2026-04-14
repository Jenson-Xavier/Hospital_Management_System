package com.example.hospitalsystem.Service.Impl;

import com.example.hospitalsystem.Entity.Appointment;
import com.example.hospitalsystem.Repository.AppointmentRepository;
import com.example.hospitalsystem.Service.AppointmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AppointmentServiceImpl implements AppointmentService {
    @Autowired
    private AppointmentRepository appointmentRepository;

    @Override
    public Long count() {
        return appointmentRepository.count();
    }

    @Override
    public void delete(Appointment appointment) {
        appointmentRepository.delete(appointment);
    }

    @Override
    public void deleteAll() {
        appointmentRepository.deleteAll();
    }

    @Override
    public void deleteAll(List<Appointment> appointments) {
        appointmentRepository.deleteAll(appointments);
    }

    @Override
    public void deleteAllById(List<Long> ids) {
        appointmentRepository.deleteAllById(ids);
    }

    @Override
    public void deleteById(Long id) {
        appointmentRepository.deleteById(id);
    }

    @Override
    public boolean existsById(Long id) {
        return appointmentRepository.existsById(id);
    }

    @Override
    public List<Appointment> findAll() {
        return appointmentRepository.findAll();
    }

    @Override
    public List<Appointment> findAllById(List<Long> ids) {
        return appointmentRepository.findAllById(ids);
    }

    @Override
    public Appointment findById(Long id) {
        return appointmentRepository.findById(id).orElse(null);
    }

    @Override
    public Appointment save(Appointment appointment) {
        return appointmentRepository.save(appointment);
    }

    @Override
    public List<Appointment> saveAll(List<Appointment> appointments) {
        return appointmentRepository.saveAll(appointments);
    }
}
