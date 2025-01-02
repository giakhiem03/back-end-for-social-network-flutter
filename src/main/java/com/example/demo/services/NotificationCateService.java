package com.example.demo.services;

import com.example.demo.models.Notification_Category;
import com.example.demo.repository.NotificationCateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NotificationCateService {
    @Autowired
    NotificationCateRepository notificationCateRepository;

    public Notification_Category getById(int id) {
        return notificationCateRepository.findById(id).orElse(null);
    }
}
