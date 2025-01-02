package com.example.demo.services;

import com.example.demo.models.User;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    @Autowired
    UserRepository userRepository;

    public List<User> getAll() {
        return userRepository.findAll();
    }

    public Optional<User> getUserById(int id) {
        return userRepository.findById(id);
    }

    public User findByUserName(String name){
        for(User user : userRepository.findAll()){
            if(user.getUsername().equals(name)){
                return user;
            }
        }
        return null;
    }

    public User saveUser(User user) {
        return userRepository.save(user);
    }

    public void deleteUser(int id) {
        userRepository.deleteById(id);
    }

    public void updateUserAvatar(int userId, String imageUrl) {
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        user.setImage(imageUrl);
        userRepository.save(user);
    }

    public UserDTO convertUserToDTO(User user) {
        return new UserDTO(
                user.getUserId(),
                user.getFullName(),
                user.getUsername(),
                user.getPassword(),
                user.getEmail(),
                user.getPhoneNumber(),
                user.getImage(),
                user.isStatus(),
                user.getRole()
        );
    }

    public List<User> searchAllByName(String name ){
        return userRepository.findByFullNameContainingIgnoreCase(name);
    }

    public User getUserByUsername(String username) {
        return userRepository.findAll().stream().filter(u->u.getUsername().equals(username)).findFirst().orElse(null);
    }

}
