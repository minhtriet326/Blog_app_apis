package com.blog.auth.utils;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record LoginRequest(
        @NotBlank(message = "This field can't be blank")
        @Email(message = "Please enter a proper email")
        String email,
        @NotBlank(message = "This field can't be blank")
        @Size(min = 4, message = "Password must be at lease 4 characters long")
        String password) {
}
