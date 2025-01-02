package com.example.demo.services;

import com.example.demo.models.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private int userId;
    private String fullName;
    private String username;
    private String email;
    private String password;
    private String phoneNumber;
    private String image;
    private boolean status;

    private Role role;

}
