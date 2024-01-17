package com.example.onlinelearningplatform.repos;

import com.example.onlinelearningplatform.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User,Long> {
    User findByEmail(String email);
}
