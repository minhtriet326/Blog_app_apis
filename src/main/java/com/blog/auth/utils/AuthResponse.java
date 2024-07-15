package com.blog.auth.utils;

import lombok.Builder;

@Builder
public record AuthResponse(String accessToken, String refreshToken) {
}
