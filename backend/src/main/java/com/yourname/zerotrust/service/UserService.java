package com.yourname.zerotrust.service;

import java.util.List;

import com.yourname.zerotrust.dto.GenericResponse;
import com.yourname.zerotrust.dto.UserDto;
import com.yourname.zerotrust.entity.User;

public interface UserService {
    GenericResponse createUser(User user);
    List<UserDto> listUsers();
    UserDto getUser(Long id);
    GenericResponse updateUser(Long id, User user);
    GenericResponse deleteUser(Long id);
}
