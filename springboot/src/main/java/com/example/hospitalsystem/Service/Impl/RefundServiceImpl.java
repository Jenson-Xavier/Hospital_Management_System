package com.example.hospitalsystem.Service.Impl;

import com.example.hospitalsystem.Entity.Refund;
import com.example.hospitalsystem.Repository.RefundRepository;
import com.example.hospitalsystem.Service.RefundService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RefundServiceImpl implements RefundService {
    @Autowired
    private RefundRepository refundRepository;

    @Override
    public Long count() {
        return refundRepository.count();
    }

    @Override
    public void delete(Refund refund) {
        refundRepository.delete(refund);
    }

    @Override
    public void deleteAll() {
        refundRepository.deleteAll();
    }

    @Override
    public void deleteAll(List<Refund> refunds) {
        refundRepository.deleteAll(refunds);
    }

    @Override
    public void deleteAllById(List<Long> ids) {
        refundRepository.deleteAllById(ids);
    }

    @Override
    public void deleteById(Long id) {
        refundRepository.deleteById(id);
    }

    @Override
    public boolean existsById(Long id) {
        return refundRepository.existsById(id);
    }

    @Override
    public List<Refund> findAll() {
        return refundRepository.findAll();
    }

    @Override
    public List<Refund> findAllById(List<Long> ids) {
        return refundRepository.findAllById(ids);
    }

    @Override
    public Refund findById(Long id) {
        return refundRepository.findById(id).orElse(null);
    }

    @Override
    public Refund save(Refund refund) {
        return refundRepository.save(refund);
    }

    @Override
    public List<Refund> saveAll(List<Refund> refunds) {
        return refundRepository.saveAll(refunds);
    }
}
