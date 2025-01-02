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
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    int postId;
    @ManyToOne(fetch = FetchType.EAGER)
    User userUpLoad;
    String postImage;
    String caption;
    int reactionQuantity;
    String postedTime;
    @JsonManagedReference
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "users_likes_posts",  // Bảng phụ để quản lý các lượt "like"
            joinColumns = @JoinColumn(name = "post_id"),  // Cột khóa ngoài cho bảng Post
            inverseJoinColumns = @JoinColumn(name = "user_id")  // Cột khóa ngoài cho bảng User
    )
    @JsonIgnoreProperties("posts") // Ngăn vòng lặp khi serialize/deserialize
    private Set<User> users = new HashSet<>();
}
