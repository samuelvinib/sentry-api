package com.sentry.api.services;

import com.sentry.api.models.User;
import com.sentry.api.repositories.TokenRepository;
import com.sentry.api.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;


public class AuthServiceTest {

    private AuthService authService;
    private UserRepository userRepository;
    private TokenRepository tokenRepository;
    private JwtUtil jwtUtil;
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp(){
        userRepository = mock(UserRepository.class);
        tokenRepository = mock(TokenRepository.class);
        jwtUtil = mock(JwtUtil.class);
        passwordEncoder = mock(PasswordEncoder.class);
    }

    @Test
    void TestRegisterUser_Success(){

        String email = "test@test.com";
        String password = "my-secret-pw";

        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());
        when(passwordEncoder.encode(password)).thenReturn("hashedPassword");

        String result = authService.registerUser(email,password);
        assertEquals("User crated successfully!", result);

        verify(userRepository, times(1)).save(any(User.class));

    }

    @Test
    void TestRegisterUser_EmailAlreadyExists(){
        String email = "test@test.com";
        String password = "my-secret-pw";

        when(userRepository.findByEmail(email)).thenReturn(Optional.of(new User()));

        Exception exception = assertThrows(RuntimeException.class, () -> {
            authenticationService.registerUser(email, password);
        });

        assertEquals("E-mail is already registered!", exception.getMessage());
    }

    @Test
    void testAuthenticateUser_Success() {
        String email = "test@example.com";
        String password = "password";
        String token = "jwt-token";

        User user = new User(1L, email, "hashedPassword");
        when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(password, "hashedPassword")).thenReturn(true);
        when(jwtUtil.generateToken(email)).thenReturn(token);

        String result = authenticationService.authenticateUser(email, password);

        assertEquals(token, result);
    }

    @Test
    void testAuthenticateUser_InvalidCredentials() {
        String email = "test@example.com";
        String password = "password";

        when(userRepository.findByEmail(email)).thenReturn(Optional.empty());

        Exception exception = assertThrows(RuntimeException.class, () -> {
            authService.authenticateUser(email, password);
        });

        assertEquals("Invalid credentials!", exception.getMessage());
    }
}
