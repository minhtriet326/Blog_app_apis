package com.blog.auth.services;

import com.blog.auth.entities.RefreshToken;
import com.blog.auth.exceptions.RefreshTokenException;
import com.blog.auth.repositories.RefreshTokenRepository;
import com.blog.entities.User;
import com.blog.exceptions.ResourceNotFoundException;
import com.blog.repositories.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.UUID;

//@Service
//public class RefreshTokenService {
//    private final RefreshTokenRepository refreshTokenRepository;
//    private final UserRepository userRepository;
//
//    public RefreshTokenService(RefreshTokenRepository refreshTokenRepository, UserRepository userRepository) {
//        this.refreshTokenRepository = refreshTokenRepository;
//        this.userRepository = userRepository;
//    }
//
//    public RefreshToken createRefreshToken(String email) {
//
//        User existingUser = userRepository.findByEmail(email)
//                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));
//
//        if(existingUser.getRefreshToken() == null) {
//
//            RefreshToken refreshToken = RefreshToken.builder()
//                    .refreshToken(UUID.randomUUID().toString())
//                    .user(existingUser)
//                    .expirationDate(new Date(System.currentTimeMillis() + 10 * 60 * 1000))
//                    .build();
//
//            refreshTokenRepository.save(refreshToken);
//
//            return refreshToken;
//        }
//
//        return existingUser.getRefreshToken();
//    }
//
//    public RefreshToken verifyRefreshToken(String refreshToken) {
//
//        RefreshToken existingRefreshToken = refreshTokenRepository.findByRefreshToken(refreshToken)
//                .orElseThrow(() -> new ResourceNotFoundException("RefreshToken", "refreshToken", refreshToken));
//
//        if(existingRefreshToken.getExpirationDate().before(new Date())) {
//            refreshTokenRepository.delete(existingRefreshToken);
//            throw new RefreshTokenException("RefreshToken is expired!");
//        }
//
//        return existingRefreshToken;
//    }
//}
@Service
public class RefreshTokenService {
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    public RefreshTokenService(UserRepository userRepository, RefreshTokenRepository refreshTokenRepository) {
        this.userRepository = userRepository;
        this.refreshTokenRepository = refreshTokenRepository;
    }

    public RefreshToken createRefreshToken(String email) {

        User existingUser = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User", "email", email));

        if(existingUser.getRefreshToken() == null) {
            RefreshToken refreshToken = RefreshToken.builder()
                    .refreshToken(UUID.randomUUID().toString())
                    .user(existingUser)
                    .expirationDate(new Date(System.currentTimeMillis() + 1440 * 60 * 1000))
                    .build();

            refreshTokenRepository.save(refreshToken);

            return refreshToken;
        }

        return existingUser.getRefreshToken();
    }

    public RefreshToken verifyRefreshToken(String refreshToken) {

        RefreshToken existingRefreshToken = refreshTokenRepository.findByRefreshToken(refreshToken)
                .orElseThrow(() -> new ResourceNotFoundException("Refresh Token", "refreshToken", refreshToken));

        if(existingRefreshToken.getExpirationDate().before(new Date())) {
            refreshTokenRepository.delete(existingRefreshToken);
            throw new RefreshTokenException("RefreshToken is expired!");
        }

        return existingRefreshToken;
    }
}
