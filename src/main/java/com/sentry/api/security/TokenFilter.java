package com.sentry.api.security;

import com.sentry.api.repositories.UserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class TokenFilter extends OncePerRequestFilter {

    @Autowired
    TokenService tokenService;

    @Autowired
    UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        if (request.getRequestURI().startsWith("/api/auth/login") ||
                request.getRequestURI().startsWith("/api/auth/register") ||
                request.getRequestURI().startsWith("/swagger-ui.html") ||
                request.getRequestURI().startsWith("/swagger-ui/") ||
                request.getRequestURI().startsWith("/v3/api-docs") ||
                request.getRequestURI().startsWith("/swagger-resources") ||
                request.getRequestURI().startsWith("/webjars/")) {
            filterChain.doFilter(request, response);
            return;
        }

        var token = this.recoverToken(request);

        if (token == null || token.trim().isEmpty()) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Missing or invalid JWT token.");
            return;
        }

        var email = tokenService.validateToken(token);
        if (email == null || email.isEmpty()) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "Invalid JWT token.");
            return;
        }

        var user = userRepository.findByEmail(email);
        if (user == null) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN, "User not found.");
            return;
        }

        var authentication = new UsernamePasswordAuthenticationToken(user, null, null);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        filterChain.doFilter(request, response);
    }

    private String recoverToken(HttpServletRequest request) {
        var authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return null;
        }

        return authHeader.substring(7).trim();
    }
}
