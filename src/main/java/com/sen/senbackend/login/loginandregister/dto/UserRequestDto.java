package com.sen.senbackend.login.loginandregister.dto;

import lombok.Builder;

@Builder
public record UserRequestDto(
        String login,
        String password
) {
}
