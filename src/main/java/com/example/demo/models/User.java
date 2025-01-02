package com.example.demo.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor

public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int userId;
    private String fullName;
    private String username;
    private String password;
    private String email;
    private String phoneNumber;
    @Lob
    private String image;
    private String backgroundImage;
    @ManyToOne(fetch = FetchType.EAGER)
    private Role role;
    private boolean status ;
    @JsonBackReference
    @ManyToMany(mappedBy = "users")
    @JsonIgnoreProperties({"userUpLoad","users"}) // Ngăn vòng lặp khi serialize/deserialize
    private Set<Post> posts = new HashSet<>();
    private String token;
}
