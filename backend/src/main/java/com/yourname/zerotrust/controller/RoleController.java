package com.yourname.zerotrust.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.yourname.zerotrust.dto.GenericResponse;
import com.yourname.zerotrust.dto.RoleRequest;
import com.yourname.zerotrust.dto.UpdateRoleRequest;
import com.yourname.zerotrust.entity.Role;
import com.yourname.zerotrust.service.RoleService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/roles")
@PreAuthorize("hasRole('ADMIN')")
@Tag(name = "Roles", description = "Role management (admin only)")
public class RoleController {
    @Autowired
    private RoleService roleService;

    @PostMapping
    @Operation(summary = "Create role", description = "Creates a new role. Built-in roles USER and ADMIN are seeded at startup.")
    public ResponseEntity<GenericResponse> createRole(@Valid @RequestBody RoleRequest request) {
        GenericResponse response = roleService.createRole(request);
        if (response.getMessage().startsWith("Error:")) {
            return ResponseEntity.badRequest().body(response);
        }
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @Operation(summary = "List roles", description = "Returns all roles in the system.")
    public ResponseEntity<List<Role>> listRoles() {
        return ResponseEntity.ok(roleService.listRoles());
    }

    @PutMapping
    @Operation(summary = "Update role", description = "Renames a role. ID and new name are passed in the request body.")
    public ResponseEntity<GenericResponse> updateRole(@Valid @RequestBody UpdateRoleRequest updateRoleRequest) {
        GenericResponse response = roleService.updateRole(
                updateRoleRequest.getId(), updateRoleRequest.getRole());
        if (response.getMessage().startsWith("Error:")) {
            return ResponseEntity.badRequest().body(response);
        }
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete role", description = "Deletes a role. Fails if the role is built-in or still assigned to users.")
    public ResponseEntity<GenericResponse> deleteRole(
            @Parameter(description = "Role ID") @PathVariable Long id) {
        GenericResponse response = roleService.deleteRole(id);
        if (response.getMessage().startsWith("Error:")) {
            return ResponseEntity.badRequest().body(response);
        }
        return ResponseEntity.ok(response);
    }
}
