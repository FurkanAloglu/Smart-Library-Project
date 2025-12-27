package com.furkan.smart_library_backend.auth;

import com.furkan.smart_library_backend.auth.dto.LoginRequest;
import com.furkan.smart_library_backend.security.JwtService;
import com.furkan.smart_library_backend.user.User;
import com.furkan.smart_library_backend.user.UserRepository;
import com.furkan.smart_library_backend.user.dto.UserResponse;
import com.furkan.smart_library_backend.auth.dto.RegisterRequest;
import com.furkan.smart_library_backend.user.Role;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;

    public UserResponse login(LoginRequest request, HttpServletResponse response) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.email(), request.password())
            );

            User user = userRepository.findByEmailAndDeletedFalse(request.email())
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));

            var userDetails = userDetailsService.loadUserByUsername(user.getEmail());

            String accessToken = jwtService.generateAccessToken(userDetails);
            String refreshToken = jwtService.generateRefreshToken(userDetails);

            addCookie(response, "accessToken", accessToken, 15 * 60);
            addCookie(response, "refreshToken", refreshToken, 7 * 24 * 60 * 60);

            return UserResponse.fromEntity(user);
        } catch (Exception e) {
            log.error("Error in AuthService.login request={}", request, e);
            throw new RuntimeException("Failed to login", e);
        }
    }

    public UserResponse register(@Valid RegisterRequest request){
        try {
            if (userRepository.existsByEmailAndDeletedFalse(request.email())) {
                throw new IllegalArgumentException("Bu email adresi zaten kullanımda.");
            }
            User user = User.builder()
                    .email(request.email())
                    .password(passwordEncoder.encode(request.password()))
                    .fullName(request.fullName())
                    .role(Role.USER)
                    .deleted(false)
                    .build();

            User savedUser = userRepository.save(user);
            return UserResponse.fromEntity(savedUser);
        } catch (Exception e) {
            log.error("Error in AuthService.register request={}", request, e);
            throw new RuntimeException("Failed to register user", e);
        }
    }

    public void logout(HttpServletResponse response) {
        try {
            addCookie(response, "accessToken", "", 0);
            addCookie(response, "refreshToken", "", 0);
        } catch (Exception e) {
            log.error("Error in AuthService.logout", e);
            throw new RuntimeException("Failed to logout", e);
        }
    }

    private void addCookie(HttpServletResponse response, String name, String value, int maxAge) {
        Cookie cookie = new Cookie(name, value);
        cookie.setHttpOnly(true);
        cookie.setSecure(false);
        cookie.setPath("/");
        cookie.setMaxAge(maxAge);
        response.addCookie(cookie);
    }
}