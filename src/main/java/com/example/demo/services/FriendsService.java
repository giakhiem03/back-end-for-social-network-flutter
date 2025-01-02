package com.example.demo.services;

import com.example.demo.models.Friends;
import com.example.demo.repository.FriendsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FriendsService {
    @Autowired
    FriendsRepository friendsRepository;

    public List<Friends> getAll() {
        return friendsRepository.findAll();
    }

    public void save(Friends friends) {
        friendsRepository.save(friends) ;
    }

    public Friends getById(int id) {
        return friendsRepository.findById(id).orElse(null);
    }

    public void deleteById(int id){
        friendsRepository.deleteById(id);
    }

    public void acceptFriend(int friendId) {
        Friends f = friendsRepository.findById(friendId).orElse(null);
        if(f!=null) {
            f.setStatusRelationship(2);
            friendsRepository.save(f);
        }
    }

}
