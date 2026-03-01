package com.yourname.zerotrust.service;

import java.util.List;

import com.yourname.zerotrust.dto.GenericResponse;
import com.yourname.zerotrust.entity.Role;

public interface RoleService {
    GenericResponse createRole(Role role);
    List<Role> listRoles();
    GenericResponse updateRole(Long id, Role role);
}
