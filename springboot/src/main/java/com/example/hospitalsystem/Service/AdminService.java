package com.example.hospitalsystem.Service;

import com.example.hospitalsystem.Entity.Admin;

import java.util.List;

public interface AdminService {
    Long count();

    void delete(Admin admin);

    void deleteAll();

    void deleteAll(List<Admin> admins);

    void deleteAllById(List<String> ids);

    void deleteById(String id);

    boolean existsById(String id);

    List<Admin> findAll();

    List<Admin> findAllById(List<String> ids);

    Admin findById(String id);

    Admin save(Admin admin);

    List<Admin> saveAll(List<Admin> admins);
}
