package com.example.hospitalsystem.Service;

import com.example.hospitalsystem.Entity.Refund;

import java.util.List;

public interface RefundService {
    Long count();

    void delete(Refund refund);

    void deleteAll();

    void deleteAll(List<Refund> refunds);

    void deleteAllById(List<Long> ids);

    void deleteById(Long id);

    boolean existsById(Long id);

    List<Refund> findAll();

    List<Refund> findAllById(List<Long> ids);

    Refund findById(Long id);

    Refund save(Refund refund);

    List<Refund> saveAll(List<Refund> refunds);
}
