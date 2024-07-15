package com.blog.auth.controllers;

import com.blog.auth.entities.ForgotPassword;
import com.blog.auth.entities.MailBody;
import com.blog.auth.repositories.ForgotPasswordRepository;
import com.blog.auth.services.MailService;
import com.blog.auth.utils.ChangePassword;
import com.blog.entities.User;
import com.blog.exceptions.ResourceNotFoundException;
import com.blog.repositories.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.Random;

@RestController
@RequestMapping("/api/v1/forgotPassword")
public class ForgotPasswordController {
    private final MailService mailService;
    private final UserRepository userRepository;
    private final ForgotPasswordRepository forgotPasswordRepository;
    private final PasswordEncoder passwordEncoder;

    public ForgotPasswordController(MailService mailService, UserRepository userRepository, ForgotPasswordRepository forgotPasswordRepository, PasswordEncoder passwordEncoder) {
        this.mailService = mailService;
        this.userRepository = userRepository;
        this.forgotPasswordRepository = forgotPasswordRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Operation(
            summary = "Send email for verification",
            description = "We will send an email include an OTP code to email which has been provided by user",
            tags = {"Forgot password", "get"}
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    content = @Content(schema = @Schema(implementation = String.class),
                                        mediaType = "application/json")
            )
    })
    @GetMapping("/verifyEmail/{email}")
    public ResponseEntity<String> verifyEmailHandler(@PathVariable String email) {
        User existingUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));

        int otp = generateOtp();

        MailBody mailBody = MailBody.builder()
                .to(email)
                .subject("OTP for your forgot password")
                .text("This is your OTP code for your forgot password request: " + otp)
                .build();

        mailService.sendSimpleMessage(mailBody);

        ForgotPassword forgotPassword = ForgotPassword.builder()
                .otp(otp)
                .expirationDate(new Date(System.currentTimeMillis() + 60 * 1000))
                .user(existingUser)
                .build();

        forgotPasswordRepository.save(forgotPassword);

        return ResponseEntity.status(HttpStatus.OK).body("Your email has been sent for verification!");
    }

    @Operation(
            summary = "Verify OTP",
            description = "We will verify the OTP which has been entered by user",
            tags = {"Forgot password","get"}
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    content = @Content(schema = @Schema(implementation = String.class),
                            mediaType = "application/json")
            )
    })
    @GetMapping("/verifyWithOtpAndUser/{otp}/{email}")
    public ResponseEntity<String> verifyWithOTPAndUser(@PathVariable Integer otp,
                                                       @PathVariable String email) {
        User existingUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));

        ForgotPassword existingForgotPassword = forgotPasswordRepository.findByOtpAndUser(otp, existingUser)
                .orElseThrow(() -> new RuntimeException("OTO is wrong! Please enter an correct OTP!"));

        if(existingForgotPassword.getExpirationDate().before(new Date())) {
            forgotPasswordRepository.delete(existingForgotPassword);
            return ResponseEntity.status(HttpStatus.GONE).body("OTP has expired!");
        }

        return ResponseEntity.status(HttpStatus.OK).body("OTP is correct, now you can change your password!");
    }

    @Operation(
            summary = "Change password",
            description = "We will change the password after checking the new password and the repeat password are the same",
            tags = {"Forgot password", "put"}
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    content = @Content(schema = @Schema(implementation = String.class),
                            mediaType = "application/json")
            )
    })
    @PutMapping("/changePassword/{email}")
    public ResponseEntity<String> changePasswordHandler(@PathVariable String email,
                                                        @Valid @RequestBody ChangePassword changePassword) {
        if(!changePassword.newPassword().equals(changePassword.repeatPassword())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Repeat password is not correct!");
        }

        String encodePassword = passwordEncoder.encode(changePassword.repeatPassword());

        userRepository.updatePassword(email, encodePassword);

        User existingUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));

        ForgotPassword existingForgotPassword = forgotPasswordRepository.findByUser(existingUser).get();

        forgotPasswordRepository.delete(existingForgotPassword);

        return ResponseEntity.status(HttpStatus.OK).body("Congratulation! Your password has been changed!");
    }

    private int generateOtp() {
        Random random = new Random();
        return random.nextInt(100_000, 999_999);
    }
}
