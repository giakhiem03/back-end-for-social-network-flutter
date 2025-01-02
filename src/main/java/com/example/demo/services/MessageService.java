package com.example.demo.services;

import com.example.demo.models.Message;
import com.example.demo.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MessageService {
    @Autowired
    MessageRepository messageRepository;
    public List<Message> getAllMessages() {
        return messageRepository.findAll();
    }

    public void save(Message message) {
        messageRepository.save(message);
    }


}
