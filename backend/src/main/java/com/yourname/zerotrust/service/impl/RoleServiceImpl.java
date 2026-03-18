package com.yourname.zerotrust.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yourname.zerotrust.dto.GenericResponse;
import com.yourname.zerotrust.entity.Role;
import com.yourname.zerotrust.repository.RoleRepository;
import com.yourname.zerotrust.service.RoleService;

@Service
public class RoleServiceImpl implements RoleService {
    @Autowired
    private RoleRepository roleRepository;

    @Override
    public GenericResponse createRole(Role role) {
        // Check for duplicate role name
        if (roleRepository.existsByName(role.getName())) {
            return new GenericResponse("Error: Role already exists");
        }
        roleRepository.save(role);
        return new GenericResponse("Role created successfully");
    }

    @Override
    public List<Role> listRoles() {
        return roleRepository.findAll();
    }

    @Override
    public GenericResponse updateRole(Long id, Role role) {
        Role existingRole = roleRepository.findById(id).orElse(null);
        if (existingRole == null) {
            return new GenericResponse("Error: Role not found");
        }

        // Check for duplicate name (if changed)
        if (role.getName() != null && !role.getName().equals(existingRole.getName())) {
            if (roleRepository.existsByName(role.getName())) {
                return new GenericResponse("Error: Role name already exists");
            }
            existingRole.setName(role.getName());
        }

        roleRepository.save(existingRole);
        return new GenericResponse("Role updated successfully");
    }
}
