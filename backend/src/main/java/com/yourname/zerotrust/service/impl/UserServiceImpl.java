package com.yourname.zerotrust.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.yourname.zerotrust.dto.GenericResponse;
import com.yourname.zerotrust.dto.UserDto;
import com.yourname.zerotrust.entity.User;
import com.yourname.zerotrust.repository.UserRepository;
import com.yourname.zerotrust.service.UserService;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public GenericResponse createUser(User user) {
        // Check for duplicate username
        if (userRepository.existsByUsername(user.getUsername())) {
            return new GenericResponse("Error: Username already exists");
        }

        // Check for duplicate email
        if (userRepository.existsByEmail(user.getEmail())) {
            return new GenericResponse("Error: Email already exists");
        }

        // Hash password before saving
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userRepository.save(user);
        return new GenericResponse("User created successfully");
    }

    @Override
    public List<UserDto> listUsers() {
        return userRepository.findAll().stream()
                .map(UserDto::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto getUser(Long id) {
        User user = userRepository.findById(id).orElse(null);
        if (user == null) {
            return null;
        }
        return UserDto.fromEntity(user);
    }

    @Override
    public GenericResponse updateUser(Long id, User user) {
        User existingUser = userRepository.findById(id).orElse(null);
        if (existingUser == null) {
            return new GenericResponse("Error: User not found");
        }

        // Check for duplicate username (if changed)
        if (user.getUsername() != null && !user.getUsername().equals(existingUser.getUsername())) {
            if (userRepository.existsByUsername(user.getUsername())) {
                return new GenericResponse("Error: Username already exists");
            }
            existingUser.setUsername(user.getUsername());
        }

        // Check for duplicate email (if changed)
        if (user.getEmail() != null && !user.getEmail().equals(existingUser.getEmail())) {
            if (userRepository.existsByEmail(user.getEmail())) {
                return new GenericResponse("Error: Email already exists");
            }
            existingUser.setEmail(user.getEmail());
        }

        // Update password if provided
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            existingUser.setPassword(passwordEncoder.encode(user.getPassword()));
        }

        // Update roles if provided
        if (user.getRoles() != null) {
            existingUser.setRoles(user.getRoles());
        }

        userRepository.save(existingUser);
        return new GenericResponse("User updated successfully");
    }

    @Override
    public GenericResponse deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            return new GenericResponse("Error: User not found");
        }
        userRepository.deleteById(id);
        return new GenericResponse("User deleted successfully");
    }
}
