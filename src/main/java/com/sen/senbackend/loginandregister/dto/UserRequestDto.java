package com.sen.senbackend.loginandregister.dto;

import lombok.Builder;

@Builder
public record UserRequestDto(
        String login,
        String password
) {
}
