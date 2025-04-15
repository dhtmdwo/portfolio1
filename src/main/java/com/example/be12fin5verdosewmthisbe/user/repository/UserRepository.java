package com.example.be12fin5verdosewmthisbe.user.repository;

import com.example.be12fin5verdosewmthisbe.user.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // Your code here
    Optional<User> findByUserId(String username);
    Optional<User> findByEmail(String email);
}
        