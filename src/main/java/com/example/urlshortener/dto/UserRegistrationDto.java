package com.example.urlshortener.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Data
public class UserRegistrationDto {
    @NotBlank
    @Size(min = 3, max = 50)
    private String username;

    @NotBlank
    @Size(min = 6, max = 100)
    private String password;

    public @NotBlank @Size(min = 3, max = 50) String getUsername() {
        return username;
    }

    public void setUsername(@NotBlank @Size(min = 3, max = 50) String username) {
        this.username = username;
    }

    public @NotBlank @Size(min = 6, max = 100) String getPassword() {
        return password;
    }

    public void setPassword(@NotBlank @Size(min = 6, max = 100) String password) {
        this.password = password;
    }
}