package com.yourname.zerotrust.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.yourname.zerotrust.dto.CreateUserRequest;
import com.yourname.zerotrust.dto.GenericResponse;
import com.yourname.zerotrust.dto.IdRequest;
import com.yourname.zerotrust.dto.UpdateUserRequest;
import com.yourname.zerotrust.dto.UserDto;
import com.yourname.zerotrust.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/users")
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Users", description = "User management (admin only)")
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping
    @Operation(summary = "Create user", description = "Creates a new user with username, email, password, and role.")
    public ResponseEntity<GenericResponse> createUser(@Valid @RequestBody CreateUserRequest request) {
        GenericResponse response = userService.createUser(request);
        if (response.getMessage().startsWith("Error:")) {
            return ResponseEntity.badRequest().body(response);
        }
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Operation(summary = "List users", description = "Returns all registered users.")
    public ResponseEntity<List<UserDto>> listUsers() {
        return ResponseEntity.ok(userService.listUsers());
    }

    @PostMapping("/get")
    @Operation(summary = "Get user by ID", description = "Fetches a single user. ID is passed in the request body.")
    public ResponseEntity<?> getUser(@Valid @RequestBody IdRequest idRequest) {
        UserDto user = userService.getUser(idRequest.getId());
        if (user == null) {
            return ResponseEntity.status(404).body(new GenericResponse("Error: User not found"));
        }
        return ResponseEntity.ok(user);
    }

    @PutMapping
    @Operation(summary = "Update user", description = "Updates email, role, or other user fields.")
    public ResponseEntity<GenericResponse> updateUser(@Valid @RequestBody UpdateUserRequest updateUserRequest) {
        GenericResponse response = userService.updateUser(updateUserRequest.getId(), updateUserRequest.getUser());
        if (response.getMessage().startsWith("Error:")) {
            return ResponseEntity.badRequest().body(response);
        }
        return ResponseEntity.ok(response);
    }

    @DeleteMapping
    @Operation(summary = "Delete user", description = "Deletes a user by ID (passed in request body).")
    public ResponseEntity<GenericResponse> deleteUser(@Valid @RequestBody IdRequest idRequest) {
        GenericResponse response = userService.deleteUser(idRequest.getId());
        if (response.getMessage().startsWith("Error:")) {
            return ResponseEntity.status(404).body(response);
        }
        return ResponseEntity.ok(response);
    }
}
