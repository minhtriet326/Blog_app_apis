package com.blog.auth.entities;

import com.blog.entities.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
public class RefreshToken {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer tokenId;

    @Column(nullable = false)
    @NotBlank(message = "Refresh token can't be blank")
    private String refreshToken;

    @Column(nullable = false)
    private Date expirationDate;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;
}
