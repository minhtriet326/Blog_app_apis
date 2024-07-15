package com.blog.auth.utils;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CurrentPassword(
        @NotBlank(message = "This field can't be blank") @Size(min = 4, message = "Password must be at least 4 characters long")
        String currentPassword) {
}
