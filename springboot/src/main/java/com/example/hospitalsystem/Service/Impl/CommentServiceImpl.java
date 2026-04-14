package com.example.hospitalsystem.Service.Impl;

import com.example.hospitalsystem.Entity.Comment;
import com.example.hospitalsystem.Repository.CommentRepository;
import com.example.hospitalsystem.Service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentServiceImpl implements CommentService {
    @Autowired
    private CommentRepository commentRepository;

    @Override
    public Long count() {
        return commentRepository.count();
    }

    @Override
    public void delete(Comment comment) {
        commentRepository.delete(comment);
    }

    @Override
    public void deleteAll() {
        commentRepository.deleteAll();
    }

    @Override
    public void deleteAll(List<Comment> comments) {
        commentRepository.deleteAll(comments);
    }

    @Override
    public void deleteAllById(List<Long> ids) {
        commentRepository.deleteAllById(ids);
    }

    @Override
    public void deleteById(Long id) {
        commentRepository.deleteById(id);
    }

    @Override
    public boolean existsById(Long id) {
        return commentRepository.existsById(id);
    }

    @Override
    public List<Comment> findAll() {
        return commentRepository.findAll();
    }

    @Override
    public List<Comment> findAllById(List<Long> ids) {
        return commentRepository.findAllById(ids);
    }

    @Override
    public Comment findById(Long id) {
        return commentRepository.findById(id).orElse(null);
    }

    @Override
    public Comment save(Comment comment) {
        return commentRepository.save(comment);
    }

    @Override
    public List<Comment> saveAll(List<Comment> comments) {
        return commentRepository.saveAll(comments);
    }
}
