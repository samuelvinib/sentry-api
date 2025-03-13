package com.sentry.api.services;

import com.sentry.api.models.User;
import com.sentry.api.repositories.UserRepository;
import com.sentry.api.security.TokenService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    private static final Logger logger = LogManager.getLogger(AuthService.class);

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, TokenService tokenService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenService = tokenService;
    }

    public String registerUser(String email, String password) {
        logger.info("Attempting to register user with email: {}", email);
        Optional<User> existingUser = userRepository.findByEmail(email);

        if (existingUser.isPresent()) {
            logger.error("E-mail {} is already registered.", email);
            throw new RuntimeException("E-mail is already registered!");
        }

        String hashedPassword = passwordEncoder.encode(password);
        User newUser = new User();
        newUser.setEmail(email);
        newUser.setPassword(hashedPassword);

        userRepository.save(newUser);
        logger.info("User successfully registered with email: {}", email);
        return "User created successfully!";
    }

    public String authenticateUser(String email, String password) {
        logger.info("Attempting to authenticate user with email: {}", email);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> {
                    logger.error("Invalid credentials for email: {}", email);
                    return new RuntimeException("Invalid credentials!");
                });

        if (!passwordEncoder.matches(password, user.getPassword())) {
            logger.error("Invalid credentials for email: {}", email);
            throw new RuntimeException("Invalid credentials!");
        }

        String token = tokenService.generateToken(email);
        logger.info("User authenticated successfully with email: {}", email);
        return token;
    }
}
