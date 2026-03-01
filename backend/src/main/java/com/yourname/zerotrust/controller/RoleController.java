package com.yourname.zerotrust.controller;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.yourname.zerotrust.dto.GenericResponse;
import com.yourname.zerotrust.dto.UpdateRoleRequest;
import com.yourname.zerotrust.entity.Role;
import com.yourname.zerotrust.service.RoleService;
@RestController
@RequestMapping("/api/roles")
public class RoleController {
    @Autowired
    private RoleService roleService;

    @PostMapping
    public ResponseEntity<GenericResponse> createRole(@RequestBody Role role) {
        GenericResponse response = roleService.createRole(role);
        if (response.getMessage().startsWith("Error:")) {
            return ResponseEntity.badRequest().body(response);
        }
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<Role>> listRoles() {
        return ResponseEntity.ok(roleService.listRoles());
    }

    @PutMapping
    public ResponseEntity<GenericResponse> updateRole(@RequestBody UpdateRoleRequest updateRoleRequest) {
        GenericResponse response = roleService.updateRole(updateRoleRequest.getId(), updateRoleRequest.getRole());
        if (response.getMessage().startsWith("Error:")) {
            return ResponseEntity.badRequest().body(response);
        }
        return ResponseEntity.ok(response);
    }
}
