package com.sen.senbackend.loginandregister.dto;

import lombok.Builder;

@Builder
public record UserResponseDto(
        String login,
        String password
) {
}
