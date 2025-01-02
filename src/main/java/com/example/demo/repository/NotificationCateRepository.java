package com.example.demo.repository;

import com.example.demo.models.Notification_Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificationCateRepository extends JpaRepository<Notification_Category,Integer> {
}
