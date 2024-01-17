package com.example.onlinelearningplatform.service;

import com.example.onlinelearningplatform.dto.UserDto;
import com.example.onlinelearningplatform.models.Role;
import com.example.onlinelearningplatform.models.User;
import com.example.onlinelearningplatform.repos.UserRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserDetailsServiceImp implements UserDetailsService {
    private final UserRepository userRepository;
    private PasswordEncoder passwordEncoder;

    public UserDetailsServiceImp(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email);

        if (user != null) {
            return new org.springframework.security.core.userdetails.User(user.getEmail(),
                    user.getPassword(),
                    mapRolesToAuthorities(user.getRole()));
        } else {
            throw new UsernameNotFoundException("Invalid username or password.");
        }
    }

    private Collection<? extends GrantedAuthority> mapRolesToAuthorities(Role role) {
        return Collections.singletonList(new SimpleGrantedAuthority(role.name()));
    }

    public boolean authenticateByEmail(String email, String password) {
        UserDetails userDetails = loadUserByUsername(email);
        return passwordEncoder.matches(password, userDetails.getPassword());
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
