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
import com.yourname.zerotrust.dto.UserDto;
import com.yourname.zerotrust.entity.User;
import com.yourname.zerotrust.service.UserService;
@RestController
@RequestMapping("/api/users")
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping
    public ResponseEntity<GenericResponse> createUser(@RequestBody User user) {
        GenericResponse response = userService.createUser(user);
        if (response.getMessage().startsWith("Error:")) {
            return ResponseEntity.badRequest().body(response);
        }
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<UserDto>> listUsers() {
        return ResponseEntity.ok(userService.listUsers());
    }

    @PostMapping("/get")
    public ResponseEntity<?> getUser(@RequestBody IdRequest idRequest) {
        UserDto user = userService.getUser(idRequest.getId());
        if (user == null) {
            return ResponseEntity.status(404).body(new GenericResponse("Error: User not found"));
        }
        return ResponseEntity.ok(user);
    }

    @PutMapping
    public ResponseEntity<GenericResponse> updateUser(@RequestBody UpdateUserRequest updateUserRequest) {
        GenericResponse response = userService.updateUser(updateUserRequest.getId(), updateUserRequest.getUser());
        if (response.getMessage().startsWith("Error:")) {
            return ResponseEntity.badRequest().body(response);
        }
        return ResponseEntity.ok(response);
    }

    @DeleteMapping
    public ResponseEntity<GenericResponse> deleteUser(@RequestBody IdRequest idRequest) {
        GenericResponse response = userService.deleteUser(idRequest.getId());
        if (response.getMessage().startsWith("Error:")) {
            return ResponseEntity.status(404).body(response);
        }
        return ResponseEntity.ok(response);
    }
}
