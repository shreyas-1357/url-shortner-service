package com.example.urlshortener.controller;

import com.example.urlshortener.dto.LoginRequest;
import com.example.urlshortener.dto.LoginResponse;
import com.example.urlshortener.dto.UserRegistrationDto;
import com.example.urlshortener.model.User;
import com.example.urlshortener.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@Valid @RequestBody UserRegistrationDto registrationDto) {
        try {
            User user = userService.registerUser(registrationDto.getUsername(), registrationDto.getPassword());
            return ResponseEntity.ok(Map.of(
                    "message", "User registered successfully",
                    "username", user.getUsername()
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()
                    )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            return ResponseEntity.ok(new LoginResponse(
                    loginRequest.getUsername(),
                    "Login successful",
                    true
            ));

        } catch (AuthenticationException e) {
            return ResponseEntity.ok(new LoginResponse(
                    loginRequest.getUsername(),
                    "Invalid username or password",
                    false
            ));
        }
    }
}