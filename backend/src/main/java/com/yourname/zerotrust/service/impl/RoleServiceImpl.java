package com.yourname.zerotrust.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.yourname.zerotrust.dto.GenericResponse;
import com.yourname.zerotrust.dto.RoleRequest;
import com.yourname.zerotrust.entity.Role;
import com.yourname.zerotrust.repository.RoleRepository;
import com.yourname.zerotrust.service.RoleService;

@Service
public class RoleServiceImpl implements RoleService {
    @Autowired
    private RoleRepository roleRepository;

    @Override
    public GenericResponse createRole(RoleRequest request) {
        if (roleRepository.existsByName(request.getName())) {
            return new GenericResponse("Error: Role already exists");
        }
        Role role = new Role();
        role.setName(request.getName());
        roleRepository.save(role);
        return new GenericResponse("Role created successfully");
    }

    @Override
    public List<Role> listRoles() {
        return roleRepository.findAll();
    }

    @Override
    public GenericResponse updateRole(Long id, RoleRequest request) {
        Role existingRole = roleRepository.findById(id).orElse(null);
        if (existingRole == null) {
            return new GenericResponse("Error: Role not found");
        }

        if (!request.getName().equals(existingRole.getName())) {
            if (roleRepository.existsByName(request.getName())) {
                return new GenericResponse("Error: Role name already exists");
            }
            existingRole.setName(request.getName());
        }

        roleRepository.save(existingRole);
        return new GenericResponse("Role updated successfully");
    }

    @Override
    @Transactional(readOnly = true)
    public GenericResponse deleteRole(Long id) {
        Role role = roleRepository.findById(id).orElse(null);
        if (role == null) {
            return new GenericResponse("Error: Role not found");
        }

        if ("USER".equals(role.getName()) || "ADMIN".equals(role.getName())) {
            return new GenericResponse("Error: Cannot delete built-in role " + role.getName());
        }

        if (role.getUsers() != null && !role.getUsers().isEmpty()) {
            return new GenericResponse("Error: Role is assigned to "
                    + role.getUsers().size() + " user(s)");
        }

        roleRepository.delete(role);
        return new GenericResponse("Role deleted successfully");
    }
}
