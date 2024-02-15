package com.example.onlinelearningplatform.service.implementations;

import com.example.onlinelearningplatform.dto.UserDto;
import com.example.onlinelearningplatform.models.Role;
import com.example.onlinelearningplatform.models.User;
import com.example.onlinelearningplatform.repos.UserRepository;
import com.example.onlinelearningplatform.service.services.UserService;
import com.example.onlinelearningplatform.token.ConfirmationToken;
import com.example.onlinelearningplatform.token.ConfirmationTokenRepository;
import com.example.onlinelearningplatform.token.ConfirmationTokenService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService, UserDetailsService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ConfirmationTokenService confirmationTokenService;

    public void save(UserDto userDto) {
        User user = new User(userDto.getEmail(), userDto.getFirstname(), userDto.getLastname(),
                passwordEncoder.encode(userDto.getPassword()), Role.USER);
        userRepository.save(user);

        // TODO: Send confirmation token
        String token = UUID.randomUUID().toString();
        ConfirmationToken confirmationToken = new ConfirmationToken(
                token,
                LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(15),
                user
        );
        confirmationTokenService.saveConfirmationToken(confirmationToken);

        // TODO: Send email
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

    @Override
    public int enableUser(String email) {
        return userRepository.enableUser(email);
    }

    @Transactional
    public String confirmToken(String token) {
        ConfirmationToken confirmationToken = confirmationTokenService
                .getToken(token)
                .orElseThrow(() ->
                        new IllegalStateException("token not found"));

        if (confirmationToken.getConfirmedAt() != null) {
            throw new IllegalStateException("email already confirmed");
        }

        LocalDateTime expiredAt = confirmationToken.getExpiresAt();

        if (expiredAt.isBefore(LocalDateTime.now())) {
            throw new IllegalStateException("token expired");
        }

        confirmationTokenService.setConfirmedAt(token);
        enableUser(
                confirmationToken.getUser().getEmail());
        return "confirmed";
    }

}