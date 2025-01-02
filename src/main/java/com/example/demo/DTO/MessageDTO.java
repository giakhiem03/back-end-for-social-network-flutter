package com.example.demo.DTO;

import com.example.demo.models.User;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class MessageDTO {
    int id;
    int userSendMessage;
    int userReceiveMessage;
    String content;
}
