package com.blog.payloads;

import com.blog.auth.entities.Role;
import jakarta.persistence.Column;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class UserDTO {
    private Integer userId;

    @NotEmpty(message = "This field can't be empty")
    @Column(name = "user_name", nullable = false, length = 100)
    @Size(min = 8, max = 30, message = "Username must be at least 8 characters and maximum 30 characters")
    private String name;

    @NotEmpty(message = "This field can't be empty")
    @Email(message = "Please enter a proper email")
    @Column(unique = true)
    private String email;

    @NotEmpty(message = "This field can't be empty")
    @Size(min = 4, message = "Password must be at least 4 characters long")
    private String password;

    private String about;
}
