package com.example.onlinelearningplatform.service.implementations;

import com.example.onlinelearningplatform.dto.UserDto;
import com.example.onlinelearningplatform.models.Role;
import com.example.onlinelearningplatform.models.User;
import com.example.onlinelearningplatform.repos.UserRepository;
import com.example.onlinelearningplatform.service.services.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void save(@Valid UserDto request) {
        User user = User.builder()
                .email(request.getEmail())
                .firstname(request.getFirstname())
                .lastname(request.getLastname())
                .password(passwordEncoder.encode(request.getPassword()))
                .role(Role.USER)
                .build();

        userRepository.save(user);
    }

    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
}