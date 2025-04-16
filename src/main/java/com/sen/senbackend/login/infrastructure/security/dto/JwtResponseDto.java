package com.sen.senbackend.login.infrastructure.security.dto;

import lombok.Builder;

@Builder
public record JwtResponseDto(
        String login,
        String token
) {
}
