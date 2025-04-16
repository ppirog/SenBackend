package com.sen.senbackend.infrastructure.errorvalidation;

import lombok.Builder;

@Builder
public record DuplicateKeyExceptionDto(
        String message
) {
}
