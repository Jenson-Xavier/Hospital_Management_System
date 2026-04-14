package com.example.hospitalsystem.Repository;

import com.example.hospitalsystem.Entity.Doctor;
import com.example.hospitalsystem.Entity.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;

public interface DoctorRepository extends JpaRepository<Doctor, String>, JpaSpecificationExecutor<Doctor>, CrudRepository<Doctor, String> {



}
