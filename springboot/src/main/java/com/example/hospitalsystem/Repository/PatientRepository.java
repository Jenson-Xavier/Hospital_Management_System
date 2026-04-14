package com.example.hospitalsystem.Repository;

import com.example.hospitalsystem.Entity.Patient;
import jakarta.transaction.Transactional;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface PatientRepository extends JpaRepository<Patient, String>, JpaSpecificationExecutor<Patient>, CrudRepository<Patient, String> {


}
