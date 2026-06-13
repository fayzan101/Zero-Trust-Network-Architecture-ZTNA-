package com.yourname.zerotrust.service;

import java.util.List;

import com.yourname.zerotrust.dto.GenericResponse;
import com.yourname.zerotrust.dto.RoleRequest;
import com.yourname.zerotrust.entity.Role;

public interface RoleService {
    GenericResponse createRole(RoleRequest request);
    List<Role> listRoles();
    GenericResponse updateRole(Long id, RoleRequest request);
    GenericResponse deleteRole(Long id);
}
