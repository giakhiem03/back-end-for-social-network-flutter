package com.example.demo.DTO;

import com.example.demo.models.Notification_Category;
import com.example.demo.models.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class NotificationDTO {
    int userIdSend;
    int userIdReceive;
    int noteId;
}
