package com.example.demo.DTO;

import com.example.demo.models.Post;
import com.example.demo.models.Role;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.FetchType;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserDTO {
    private int userId;
    private String fullName;
    private String username;
    private String password;
    private String email;
    private String phoneNumber;
    private String image;
    private String backgroundImage;
    private int role;
    private boolean status ;
    private List<Integer> posts;
}
