package com.example.hospitalsystem.Service.Impl;

import com.example.hospitalsystem.Entity.Admin;
import com.example.hospitalsystem.Repository.AdminRepository;
import com.example.hospitalsystem.Service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminServiceImpl implements AdminService {
    @Autowired
    private AdminRepository adminRepository;

    @Override
    public Long count() {
        return adminRepository.count();
    }

    @Override
    public void delete(Admin admin) {
        adminRepository.delete(admin);
    }

    @Override
    public void deleteAll() {
        adminRepository.deleteAll();
    }

    @Override
    public void deleteAll(List<Admin> admins) {
        adminRepository.deleteAll(admins);
    }

    @Override
    public void deleteAllById(List<String> ids) {
        adminRepository.deleteAllById(ids);
    }

    @Override
    public void deleteById(String id) {
        adminRepository.deleteById(id);
    }

    @Override
    public boolean existsById(String id) {
        return adminRepository.existsById(id);
    }

    @Override
    public List<Admin> findAll() {
        return adminRepository.findAll();
    }

    @Override
    public List<Admin> findAllById(List<String> ids) {
        return adminRepository.findAllById(ids);
    }

    @Override
    public Admin findById(String id) {
        return adminRepository.findById(id).orElse(null);
    }

    @Override
    public Admin save(Admin admin) {
        return adminRepository.save(admin);
    }

    @Override
    public List<Admin> saveAll(List<Admin> admins) {
        return adminRepository.saveAll(admins);
    }
}