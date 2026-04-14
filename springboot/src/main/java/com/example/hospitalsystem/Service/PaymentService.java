package com.example.hospitalsystem.Service;

import com.example.hospitalsystem.Entity.Payment;

import java.util.List;

public interface PaymentService {
    Long count();

    void delete(Payment payment);

    void deleteAll();

    void deleteAll(List<Payment> payments);

    void deleteAllById(List<Long> ids);

    void deleteById(Long id);

    boolean existsById(Long id);

    List<Payment> findAll();

    List<Payment> findAllById(List<Long> ids);

    Payment findById(Long id);

    Payment save(Payment payment);

    List<Payment> saveAll(List<Payment> payments);
}
