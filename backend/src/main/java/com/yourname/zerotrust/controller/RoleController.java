package com.yourname.zerotrust.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
// import removed: PathVariable
import com.yourname.zerotrust.dto.UpdateRoleRequest;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.yourname.zerotrust.dto.GenericResponse;
import com.yourname.zerotrust.entity.Role;
import com.yourname.zerotrust.service.RoleService;

@RestController
@RequestMapping("/api/roles")
public class RoleController {
    @Autowired
    private RoleService roleService;

    @PostMapping
    public ResponseEntity<GenericResponse> createRole(@RequestBody Role role) {
        return ResponseEntity.ok(roleService.createRole(role));
    }

    @GetMapping
    public ResponseEntity<List<Role>> listRoles() {
        return ResponseEntity.ok(roleService.listRoles());
    }

    @PutMapping
    public ResponseEntity<GenericResponse> updateRole(@RequestBody UpdateRoleRequest updateRoleRequest) {
        return ResponseEntity.ok(roleService.updateRole(updateRoleRequest.getId(), updateRoleRequest.getRole()));
    }
}
