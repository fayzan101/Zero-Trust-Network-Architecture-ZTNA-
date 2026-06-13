package com.yourname.zerotrust.service.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yourname.zerotrust.dto.CreateUserRequest;
import com.yourname.zerotrust.dto.GenericResponse;
import com.yourname.zerotrust.dto.UserDto;
import com.yourname.zerotrust.entity.Role;
import com.yourname.zerotrust.entity.User;
import com.yourname.zerotrust.repository.RoleRepository;
import com.yourname.zerotrust.repository.UserRepository;
import com.yourname.zerotrust.service.UserService;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public GenericResponse createUser(CreateUserRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            return new GenericResponse("Error: Username already exists");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            return new GenericResponse("Error: Email already exists");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setMfaEnabled(false);

        String roleName = request.getRole() != null ? request.getRole() : "USER";
        roleRepository.findByName(roleName).ifPresent(role -> {
            Set<Role> roles = new HashSet<>();
            roles.add(role);
            user.setRoles(roles);
        });

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

        if (user.getUsername() != null && !user.getUsername().equals(existingUser.getUsername())) {
            if (userRepository.existsByUsername(user.getUsername())) {
                return new GenericResponse("Error: Username already exists");
            }
            existingUser.setUsername(user.getUsername());
        }

        if (user.getEmail() != null && !user.getEmail().equals(existingUser.getEmail())) {
            if (userRepository.existsByEmail(user.getEmail())) {
                return new GenericResponse("Error: Email already exists");
            }
            existingUser.setEmail(user.getEmail());
        }

        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            existingUser.setPassword(passwordEncoder.encode(user.getPassword()));
        }

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
