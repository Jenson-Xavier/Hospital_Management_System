package com.example.hospitalsystem.Service.Impl;

import com.example.hospitalsystem.Entity.Payment;
import com.example.hospitalsystem.Repository.PaymentRepository;
import com.example.hospitalsystem.Service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PaymentServiceImpl implements PaymentService {
    @Autowired
    private PaymentRepository paymentRepository;

    @Override
    public Long count() {
        return paymentRepository.count();
    }

    @Override
    public void delete(Payment payment) {
        paymentRepository.delete(payment);
    }

    @Override
    public void deleteAll() {
        paymentRepository.deleteAll();
    }

    @Override
    public void deleteAll(List<Payment> payments) {
        paymentRepository.deleteAll(payments);
    }

    @Override
    public void deleteAllById(List<Long> ids) {
        paymentRepository.deleteAllById(ids);
    }

    @Override
    public void deleteById(Long id) {
        paymentRepository.deleteById(id);
    }

    @Override
    public boolean existsById(Long id) {
        return paymentRepository.existsById(id);
    }

    @Override
    public List<Payment> findAll() {
        return paymentRepository.findAll();
    }

    @Override
    public List<Payment> findAllById(List<Long> ids) {
        return paymentRepository.findAllById(ids);
    }

    @Override
    public Payment findById(Long id) {
        return paymentRepository.findById(id).orElse(null);
    }

    @Override
    public Payment save(Payment payment) {
        return paymentRepository.save(payment);
    }

    @Override
    public List<Payment> saveAll(List<Payment> payments) {
        return paymentRepository.saveAll(payments);
    }
}
