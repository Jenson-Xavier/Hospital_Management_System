package com.example.hospitalsystem.Repository;

import com.example.hospitalsystem.Entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {

}
