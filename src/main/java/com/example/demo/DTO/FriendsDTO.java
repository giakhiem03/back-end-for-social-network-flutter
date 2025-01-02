package com.example.demo.DTO;

import com.example.demo.models.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class FriendsDTO {
    int id;
    int userIdSend;
    int userIdReceive;
    int statusRelationship;
}
