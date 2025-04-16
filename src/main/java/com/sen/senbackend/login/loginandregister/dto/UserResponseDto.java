package com.sen.senbackend.login.loginandregister.dto;

import lombok.Builder;

@Builder
public record UserResponseDto(
        String login,
        String password
) {
}
