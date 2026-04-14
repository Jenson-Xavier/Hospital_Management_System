package com.example.hospitalsystem.Repository;

import com.example.hospitalsystem.Entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, String> {

}
