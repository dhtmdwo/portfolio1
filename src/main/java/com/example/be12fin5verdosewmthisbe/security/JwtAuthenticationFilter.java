package com.example.be12fin5verdosewmthisbe.security;

import com.example.be12fin5verdosewmthisbe.user.service.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;


@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        String token = jwtTokenProvider.resolveToken(request);
        System.out.println("JWT 토큰: " + token); // ✅ 토큰 유무 확인
        if (token != null && jwtTokenProvider.validateToken(token)) {
            System.out.println("토큰 유효: " + jwtTokenProvider.validateToken(token));

            String email = jwtTokenProvider.getEmailFromToken(token); // 또는 getUsernameFromToken()
            System.out.println("이메일: " + email);


            var authentication = jwtTokenProvider.getAuthentication(token);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        filterChain.doFilter(request, response);
    }
}
