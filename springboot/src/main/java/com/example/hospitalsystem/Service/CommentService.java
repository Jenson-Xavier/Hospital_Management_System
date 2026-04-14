package com.example.hospitalsystem.Service;

import com.example.hospitalsystem.Entity.Comment;

import java.util.List;

public interface CommentService {
    Long count();

    void delete(Comment comment);

    void deleteAll();

    void deleteAll(List<Comment> comments);

    void deleteAllById(List<Long> ids);

    void deleteById(Long id);

    boolean existsById(Long id);

    List<Comment> findAll();

    List<Comment> findAllById(List<Long> ids);

    Comment findById(Long id);

    Comment save(Comment comment);

    List<Comment> saveAll(List<Comment> comments);
}
