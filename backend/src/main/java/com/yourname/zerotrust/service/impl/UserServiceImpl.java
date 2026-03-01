package com.yourname.zerotrust.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yourname.zerotrust.dto.GenericResponse;
import com.yourname.zerotrust.entity.User;
import com.yourname.zerotrust.repository.UserRepository;
import com.yourname.zerotrust.service.UserService;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;

    @Override
    public GenericResponse createUser(User user) {
        userRepository.save(user);
        return new GenericResponse("User created");
    }

    @Override
    public List<User> listUsers() {
        return userRepository.findAll();
    }

    @Override
    public User getUser(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    @Override
    public GenericResponse updateUser(Long id, User user) {
        user.setId(id);
        userRepository.save(user);
        return new GenericResponse("User updated");
    }

    @Override
    public GenericResponse deleteUser(Long id) {
        userRepository.deleteById(id);
        return new GenericResponse("User deleted");
    }
}
