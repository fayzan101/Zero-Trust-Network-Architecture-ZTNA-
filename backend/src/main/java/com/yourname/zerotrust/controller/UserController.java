package com.yourname.zerotrust.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.yourname.zerotrust.dto.GenericResponse;
import com.yourname.zerotrust.dto.IdRequest;
import com.yourname.zerotrust.dto.UpdateUserRequest;
import com.yourname.zerotrust.entity.User;
import com.yourname.zerotrust.service.UserService;

@RestController
@RequestMapping("/api/users")
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping
    public ResponseEntity<GenericResponse> createUser(@RequestBody User user) {
        return ResponseEntity.ok(userService.createUser(user));
    }

    @GetMapping
    public ResponseEntity<List<User>> listUsers() {
        return ResponseEntity.ok(userService.listUsers());
    }

    @PostMapping("/get")
    public ResponseEntity<User> getUser(@RequestBody IdRequest idRequest) {
        return ResponseEntity.ok(userService.getUser(idRequest.getId()));
    }

    @PutMapping
    public ResponseEntity<GenericResponse> updateUser(@RequestBody UpdateUserRequest updateUserRequest) {
        return ResponseEntity.ok(userService.updateUser(updateUserRequest.getId(), updateUserRequest.getUser()));
    }

    @DeleteMapping
    public ResponseEntity<GenericResponse> deleteUser(@RequestBody IdRequest idRequest) {
        return ResponseEntity.ok(userService.deleteUser(idRequest.getId()));
    }
}
