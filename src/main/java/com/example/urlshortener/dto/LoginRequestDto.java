package com.example.urlshortener.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class LoginRequestDto {
    @NotBlank(message = "Username cannot be empty")
    private String username;

    @NotBlank(message = "Password cannot be empty")
    private String password;

    public @NotBlank(message = "Password cannot be empty") String getPassword() {
        return password;
    }

    public void setPassword(@NotBlank(message = "Password cannot be empty") String password) {
        this.password = password;
    }

    public @NotBlank(message = "Username cannot be empty") String getUsername() {
        return username;
    }

    public void setUsername(@NotBlank(message = "Username cannot be empty") String username) {
        this.username = username;
    }
}