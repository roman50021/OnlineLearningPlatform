package com.example.onlinelearningplatform.service.implementations;

import com.example.onlinelearningplatform.models.Role;
import com.example.onlinelearningplatform.models.User;
import com.example.onlinelearningplatform.repos.UserRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceImplTest {

    @Test
    public void testAuthenticateByEmail_EnabledFalse(){
        UserRepository userRepository = Mockito.mock(UserRepository.class);
        PasswordEncoder passwordEncoder = Mockito.mock(PasswordEncoder.class);

        UserServiceImpl userService = new UserServiceImpl(userRepository, passwordEncoder);

        String email = "test@example.com";
        String password = "password";
        User user = new User(email, "John", "Doe", passwordEncoder.encode(password), Role.USER);
        Mockito.when(userRepository.findByEmail(email)).thenReturn(user);
        Mockito.when(passwordEncoder.matches(password, user.getPassword())).thenReturn(true);
        user.setEnabled(false);
        user.setEnabled(false);

        boolean isAuthenticated = userService.authenticateByEmail(email, password);

        assertFalse(isAuthenticated);
    }

    @Test
    public void testAuthenticateByEmail_SuccessfulAuthentication() {
        // Создание мок-объектов
        UserRepository userRepository = Mockito.mock(UserRepository.class);
        PasswordEncoder passwordEncoder = Mockito.mock(PasswordEncoder.class);

        // Создание экземпляра класса UserServiceImpl с использованием мок-объектов
        UserServiceImpl userService = new UserServiceImpl(userRepository, passwordEncoder);

        // Задание значений для теста
        String email = "test@example.com";
        String password = "password";
        User user = new User(email, "John", "Doe", passwordEncoder.encode(password), Role.USER);
        Mockito.when(userRepository.findByEmail(email)).thenReturn(user);
        Mockito.when(passwordEncoder.matches(password, user.getPassword())).thenReturn(true);
        user.setEnabled(true);
        user.setLocked(false);

        // Вызов метода, который тестируется
        boolean isAuthenticated = userService.authenticateByEmail(email, password);

        // Проверка результата
        assertTrue(isAuthenticated);
    }

    @Test
    public void testAuthenticateByEmail_UserNotFound() {
        // Создание мок-объектов
        UserRepository userRepository = Mockito.mock(UserRepository.class);
        PasswordEncoder passwordEncoder = Mockito.mock(PasswordEncoder.class);

        // Создание экземпляра класса UserServiceImpl с использованием мок-объектов
        UserServiceImpl userService = new UserServiceImpl(userRepository, passwordEncoder);

        // Задание значений для теста
        String email = "test@example.com";
        String password = "password";
        Mockito.when(userRepository.findByEmail(email)).thenReturn(null);

        // Вызов метода, который тестируется
        boolean isAuthenticated = userService.authenticateByEmail(email, password);

        // Проверка результата
        assertFalse(isAuthenticated);
    }
}