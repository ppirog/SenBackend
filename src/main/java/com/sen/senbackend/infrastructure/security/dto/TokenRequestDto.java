package com.sen.senbackend.infrastructure.security.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

@Builder
public record TokenRequestDto(

        @NotNull(message = "{login.not.null}")
        @NotEmpty(message = "{login.not.empty}")
        @NotBlank(message = "{login.not.blank}")
        String login,

        @NotNull(message = "{password.not.null}")
        @NotEmpty(message = "{password.not.empty}")
        @NotBlank(message = "{password.not.blank}")
        String password
) {
}
