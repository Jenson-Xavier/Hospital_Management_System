package com.example.hospitalsystem.Service;

import com.example.hospitalsystem.Entity.User;

import java.util.List;

public interface UserService {
    Long count();

    void delete(User user);

    void deleteAll();

    void deleteAll(List<User> users);

    void deleteAllById(List<String> ids);

    void deleteById(String id);

    boolean existsById(String id);

    List<User> findAll();

    List<User> findAllById(List<String> ids);

    User findById(String id);

    User save(User user);

    List<User> saveAll(List<User> users);
}
