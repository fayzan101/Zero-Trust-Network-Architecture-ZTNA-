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
        roleRepository.save(role);
        return new GenericResponse("Role created");
    }

    @Override
    public List<Role> listRoles() {
        return roleRepository.findAll();
    }

    @Override
    public GenericResponse updateRole(Long id, Role role) {
        role.setId(id);
        roleRepository.save(role);
        return new GenericResponse("Role updated");
    }
}
