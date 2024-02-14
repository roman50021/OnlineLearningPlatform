package com.example.onlinelearningplatform.service.implementations;

import com.example.onlinelearningplatform.dto.UserDto;
import com.example.onlinelearningplatform.models.Role;
import com.example.onlinelearningplatform.models.User;
import com.example.onlinelearningplatform.repos.UserRepository;
import com.example.onlinelearningplatform.service.services.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService, UserDetailsService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public void save(UserDto userDto) {
        User user = new User(userDto.getEmail(), userDto.getFirstname(), userDto.getLastname(),
                passwordEncoder.encode(userDto.getPassword()), Role.USER);
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

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // Найти пользователя в базе данных по его электронной почте
        User user = userRepository.findByEmail(email);

        // Проверить, существует ли пользователь с заданной электронной почтой
        if (user == null) {
            throw new UsernameNotFoundException("User not found with email: " + email);
        }

        // Вернуть объект UserDetails, используя информацию о пользователе из базы данных
        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getEmail()) // Имя пользователя равно электронной почте
                .password(user.getPassword())
                .disabled(!user.isEnabled()) // Отключить пользователя, если он не включен
                .accountLocked(user.isLocked()) // Заблокировать учетную запись пользователя, если она заблокирована
                .authorities(Collections.singleton(new SimpleGrantedAuthority(user.getRole().name())))
                .build();
    }
    public boolean authenticateByEmail(String email, String password) {
        User user = userRepository.findByEmail(email);
        if (user != null) {
            if (user.isEnabled()) {
                if (!user.isLocked() && passwordEncoder.matches(password, user.getPassword())) {
                    // Успешная аутентификация
                    log.info("User logged in: " + email); // Логирование успешной авторизации
                    return true;
                } else {
                    System.out.println("User account locked or password incorrect");
                }
            } else {
                System.out.println("User account disabled");
            }
        } else {
            System.out.println("User not found");
        }

        return false; // Если пользователь не найден или не прошел проверку, вернуть false
    }

    public List<UserDto> findAllUsers() {
        List<User> users = userRepository.findAll();
        return users.stream().map((user) -> convertEntityToDto(user))
                .collect(Collectors.toList());
    }

    private UserDto convertEntityToDto(User user){
        UserDto userDto = new UserDto();
        userDto.setEmail(user.getEmail());
        return userDto;
    }

}