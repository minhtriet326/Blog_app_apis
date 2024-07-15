package com.blog.auth.controllers;

import com.blog.auth.services.JwtService;
import com.blog.auth.utils.ChangePassword;
import com.blog.auth.utils.CurrentPassword;
import com.blog.repositories.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/resetPassword")
public class ResetPasswordController {
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    public ResetPasswordController(JwtService jwtService, AuthenticationManager authenticationManager, PasswordEncoder passwordEncoder, UserRepository userRepository) {
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
        this.passwordEncoder = passwordEncoder;
        this.userRepository = userRepository;
    }

    @Operation(
            summary = "Verify current password",
            description = "We will verify the current password which has been provided by user",
            tags = {"Reset password", "post"}
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    content = @Content(schema = @Schema(implementation = String.class),
                                        mediaType = "application/json")
            )
    })
    @PostMapping("/verifyCurrentPassword")
    public ResponseEntity<String> verifyCurrentPasswordHandler(@NonNull HttpServletRequest request,
                                                               @Valid @RequestBody CurrentPassword currentPassword) {

        String authHeader = request.getHeader("Authorization");

        String token = authHeader.substring(7);

        String extractEmail = jwtService.extractEmail(token);

        try {

            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    extractEmail,
                    currentPassword.currentPassword()
            );

            authenticationManager.authenticate(authentication);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Current password is not correct");
        }

        return ResponseEntity.status(HttpStatus.OK).body("Now you can reset your password");
    }

    @Operation(
            summary = "Change password",
            description = "We will change the current password by the new password which has been provided by user",
            tags = {"Reset password", "put"}
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    content = @Content(schema = @Schema(implementation = String.class),
                            mediaType = "application/json")
            )
    })
    @PutMapping("/changePassword")
    public ResponseEntity<String> changePasswordHandler(@NonNull HttpServletRequest request,
                                                        @Valid @RequestBody ChangePassword changePassword) {
        if(!changePassword.newPassword().equals(changePassword.repeatPassword())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Repeat password is not correct!");
        }

        // extract email
        String authHeader = request.getHeader("Authorization");

        String token = authHeader.substring(7);

        String extractEmail = jwtService.extractEmail(token);

        // encode password
        String encodePassword = passwordEncoder.encode(changePassword.repeatPassword());

        // update password
        userRepository.updatePassword(extractEmail, encodePassword);

        return ResponseEntity.status(HttpStatus.OK).body("Congratulation! Your password has been changed!");
    }
}
