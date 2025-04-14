package com.example.be12fin5verdosewmthisbe.user.service;

import com.example.be12fin5verdosewmthisbe.user.model.User;
import com.example.be12fin5verdosewmthisbe.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {
    // Your code here
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> result = userRepository.findByUserId(username);

        if (result.isPresent()) {
            User user = result.get();
            return user;
        }

        return null;
    } // 로그인



}
        