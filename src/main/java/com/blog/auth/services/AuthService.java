package com.blog.auth.services;

import com.blog.auth.entities.RefreshToken;
import com.blog.auth.entities.Role;
import com.blog.auth.repositories.RoleRepository;
import com.blog.auth.utils.AuthResponse;
import com.blog.auth.utils.LoginRequest;
import com.blog.auth.utils.RegisterRequest;
import com.blog.entities.User;
import com.blog.exceptions.CustomUniqueConstraintViolationException;
import com.blog.exceptions.ResourceNotFoundException;
import com.blog.repositories.UserRepository;
import com.blog.utils.AppConstants;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final RefreshTokenService refreshTokenService;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RoleRepository roleRepository;

    public AuthService(UserRepository userRepository, RefreshTokenService refreshTokenService, AuthenticationManager authenticationManager, PasswordEncoder passwordEncoder, JwtService jwtService, RoleRepository roleRepository) {
        this.userRepository = userRepository;
        this.refreshTokenService = refreshTokenService;
        this.authenticationManager = authenticationManager;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.roleRepository = roleRepository;
    }

    public AuthResponse Login(LoginRequest loginRequest) {
        User existingUser = userRepository.findByEmail(loginRequest.email())
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", loginRequest.email()));

        try{

            Authentication authentication = new UsernamePasswordAuthenticationToken(
                    loginRequest.email(),
                    loginRequest.password()
            );

            authenticationManager.authenticate(authentication);

        } catch (AuthenticationException e) {

            throw new BadCredentialsException("Invalid username or password!");

        }

        RefreshToken refreshToken = refreshTokenService.createRefreshToken(loginRequest.email());

        String accessToken = jwtService.generateToken(existingUser);

        return AuthResponse.builder()
                .refreshToken(refreshToken.getRefreshToken())
                .accessToken(accessToken)
                .build();
    }

    public AuthResponse Register(RegisterRequest registerRequest) {
        if(userRepository.existsByEmail(registerRequest.email())) {
            throw new CustomUniqueConstraintViolationException("This email address is already in use!");
        }

        Role role = roleRepository.findById(AppConstants.ROLE_USER)
                .orElseThrow(() -> new ResourceNotFoundException("Role", "roleId", Integer.toString(AppConstants.ROLE_USER)));

        Set<Role> roles = Set.of(role);

        User user = User.builder()
                .name(registerRequest.name())
                .email(registerRequest.email())
                .password(passwordEncoder.encode(registerRequest.password()))
                .roles(roles)
                .build();

        userRepository.save(user);

        RefreshToken refreshToken = refreshTokenService.createRefreshToken(registerRequest.email());

        String accessToken = jwtService.generateToken(user);

        return AuthResponse.builder()
                .refreshToken(refreshToken.getRefreshToken())
                .accessToken(accessToken)
                .build();
    }
}
