package com.example.demo.services;

import com.example.demo.models.Role;
import com.example.demo.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class RoleService {
    @Autowired
    RoleRepository roleRepository;

    public Role getRoles(int id) {
        for (Role role : roleRepository.findAll()) {
            if (role.getRoleId()==id) {
                return role;
            }
        }
        return null;
    }

    public List<Role> getAllRoles() {
        return roleRepository.findAll();
    }

}
