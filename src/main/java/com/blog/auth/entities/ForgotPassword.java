package com.blog.auth.entities;

import com.blog.entities.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class ForgotPassword {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer fpid;

    @NotNull(message = "This field can't be null")
    private int otp;

    @NotNull(message = "This field can't be null")
    private Date expirationDate;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;
}
