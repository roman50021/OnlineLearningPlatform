package com.example.onlinelearningplatform.service.services;

import com.example.onlinelearningplatform.dto.UserDto;
import com.example.onlinelearningplatform.models.Role;
import com.example.onlinelearningplatform.models.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.Collection;
import java.util.List;

public interface UserService {
    void save(UserDto userDto);
    User findByEmail(String email);
    List<User> getAllUsers();

}

