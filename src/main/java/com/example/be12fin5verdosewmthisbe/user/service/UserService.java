package com.example.be12fin5verdosewmthisbe.user.service;

import com.example.be12fin5verdosewmthisbe.common.CustomException;
import com.example.be12fin5verdosewmthisbe.common.ErrorCode;
import com.example.be12fin5verdosewmthisbe.security.JwtTokenProvider;
import com.example.be12fin5verdosewmthisbe.user.model.User;
import com.example.be12fin5verdosewmthisbe.user.model.dto.UserRegisterDto;
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
    private final JwtTokenProvider jwtTokenProvider;

    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> result = userRepository.findByUserId(username);

        if (result.isPresent()) {
            User user = result.get();
            return user;
        }

        return null;
    }

    public UserRegisterDto.SignupResponse signUp(UserRegisterDto.SignupRequest dto) {



        String encodedPassword = passwordEncoder.encode(dto.getPassword());
        User user = userRepository.save(dto.toEntity(encodedPassword));
        return UserRegisterDto.SignupResponse.from(user);
    }


    public String login(String email, String rawPassword) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND) );
        if (!passwordEncoder.matches(rawPassword, user.getPassword())) {
            throw new CustomException(ErrorCode.INVALID_PASSWORD);
        }
        return jwtTokenProvider.createToken(user.getEmail());
    }


}
        