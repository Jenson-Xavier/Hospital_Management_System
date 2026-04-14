package com.example.hospitalsystem.Service;

import com.example.hospitalsystem.Entity.Appointment;

import java.util.List;

public interface AppointmentService {
    Long count();

    void delete(Appointment appointment);

    void deleteAll();

    void deleteAll(List<Appointment> appointments);

    void deleteAllById(List<Long> ids);

    void deleteById(Long id);

    boolean existsById(Long id);

    List<Appointment> findAll();

    List<Appointment> findAllById(List<Long> ids);

    Appointment findById(Long id);

    Appointment save(Appointment appointment);

    List<Appointment> saveAll(List<Appointment> appointments);
}
