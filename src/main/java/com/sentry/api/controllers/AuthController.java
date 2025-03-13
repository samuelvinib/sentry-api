package com.sentry.api.controllers;

import com.sentry.api.dtos.auth.AuthRequest;
import com.sentry.api.dtos.auth.AuthResponse;
import com.sentry.api.dtos.auth.RegisterRequest;
import com.sentry.api.config.ErrorResponse;
import com.sentry.api.services.AuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<Object> register(@RequestBody RegisterRequest request) {
        try {
            logger.info("Attempting to register user with email: {}", request.getEmail());
            String response = authService.registerUser(request.getEmail(), request.getPassword());
            logger.info("User successfully registered with email: {}", request.getEmail());
            return ResponseEntity.ok(response);
        } catch (RuntimeException ex) {
            logger.error("Error registering user with email: {} - {}", request.getEmail(), ex.getMessage());
            ErrorResponse errorResponse = new ErrorResponse(ex.getMessage(), "Please check your request");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<Object> login(@RequestBody AuthRequest request) {
        try {
            logger.info("Attempting to login user with email: {}", request.getEmail());
            String token = authService.authenticateUser(request.getEmail(), request.getPassword());
            logger.info("User successfully logged in with email: {}", request.getEmail());
            return ResponseEntity.ok(new AuthResponse(token));
        } catch (RuntimeException ex) {
            logger.error("Login failed for user with email: {} - {}", request.getEmail(), ex.getMessage());
            ErrorResponse errorResponse = new ErrorResponse(ex.getMessage(), "Invalid credentials");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        }
    }
}
