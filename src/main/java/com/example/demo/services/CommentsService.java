package com.example.demo.services;

import com.example.demo.models.Comments;
import com.example.demo.repository.CommentsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentsService {
    @Autowired
    CommentsRepository commentsRepository;

    public List<Comments> getAllCmt() {
        return commentsRepository.findAll();
    }

    public Comments getCmtById(int id) {
        return commentsRepository.findById(id).orElse(null);
    }

    public void deleteCmt(Comments comment) {
        commentsRepository.delete(comment);
    }

    public void save(Comments comments) {
        commentsRepository.save(comments);
    }

}
