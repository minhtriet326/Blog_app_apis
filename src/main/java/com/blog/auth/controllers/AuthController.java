package com.blog.auth.controllers;

import com.blog.auth.entities.RefreshToken;
import com.blog.auth.services.AuthService;
import com.blog.auth.services.JwtService;
import com.blog.auth.services.RefreshTokenService;
import com.blog.auth.utils.AuthResponse;
import com.blog.auth.utils.LoginRequest;
import com.blog.auth.utils.RefreshTokenRequest;
import com.blog.auth.utils.RegisterRequest;
import com.blog.entities.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;
    private final RefreshTokenService refreshTokenService;
    private final JwtService jwtService;

    public AuthController(AuthService authService, RefreshTokenService refreshTokenService, JwtService jwtService) {
        this.authService = authService;
        this.refreshTokenService = refreshTokenService;
        this.jwtService = jwtService;
    }

    @Operation(
            summary = "Login",
            description = "Login by Email and Password",
            tags = {"Auth", "post"}
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    content = @Content(schema = @Schema(implementation = AuthResponse.class),
                                        mediaType = "application/json")
            )
    })
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> loginHandler(@Valid @RequestBody LoginRequest loginRequest) {
        return ResponseEntity.status(HttpStatus.OK).body(authService.Login(loginRequest));
    }

    @Operation(
            summary = "Register",
            description = "Register by providing Name, Email and Password",
            tags = {"Auth", "post"}
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    content = @Content(schema = @Schema(implementation = AuthResponse.class),
                            mediaType = "application/json")
            )
    })
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> registerHandler(@Valid @RequestBody RegisterRequest registerRequest) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.Register(registerRequest));
    }

    @Operation(
            summary = "Refresh Access Token",
            description = "Refresh Access Token by checking expiration date of RefreshToken",
            tags = {"Auth", "get"}
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "201",
                    content = @Content(schema = @Schema(implementation = AuthResponse.class),
                            mediaType = "application/json")
            )
    })
    // tạo accessToken mới
    @PostMapping("/refreshAccessToken")
    public ResponseEntity<AuthResponse> refreshAccessToken(@RequestBody RefreshTokenRequest refreshTokenRequest) {
        // check nếu còn hạn cái refreshToken thì làm tiếp ko thì kêu login lại
        RefreshToken refreshToken = refreshTokenService.verifyRefreshToken(refreshTokenRequest.refreshToken());

        //tạo accessToken mới
        User existingUser = refreshToken.getUser();

        String newAccessToken = jwtService.generateToken(existingUser);

        return ResponseEntity.status(HttpStatus.CREATED).body(AuthResponse.builder()
                .refreshToken(refreshTokenRequest.refreshToken())
                .accessToken(newAccessToken)
                .build());
    }
}
