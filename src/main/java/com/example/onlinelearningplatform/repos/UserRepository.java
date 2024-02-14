package com.example.onlinelearningplatform.repos;

import com.example.onlinelearningplatform.models.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Transactional
@Repository
public interface UserRepository extends JpaRepository<User,Long> {
    User findByEmail(String email);
}
