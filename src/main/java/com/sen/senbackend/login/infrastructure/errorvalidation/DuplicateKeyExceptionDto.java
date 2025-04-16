package com.sen.senbackend.login.infrastructure.errorvalidation;

import lombok.Builder;

@Builder
public record DuplicateKeyExceptionDto(
        String message
) {
}
