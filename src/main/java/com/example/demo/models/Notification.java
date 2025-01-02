package com.example.demo.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @ManyToOne(fetch = FetchType.EAGER)
    private User userIdSend;
    @ManyToOne(fetch = FetchType.EAGER)
    private User userIdReceive;
    @ManyToOne(fetch = FetchType.EAGER)
    private Notification_Category notificationCategory;
}
