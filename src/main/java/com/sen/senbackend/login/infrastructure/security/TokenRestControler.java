package com.sen.senbackend.login.infrastructure.security;

import com.sen.senbackend.login.infrastructure.security.dto.JwtResponseDto;
import com.sen.senbackend.login.infrastructure.security.dto.TokenRequestDto;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Log4j2
@RestController
@RequestMapping("/api/login")
@AllArgsConstructor
class TokenRestControler {

    private final JwtAuthFacade  jwtAuthFacade;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<JwtResponseDto> fetchToken(@RequestBody @Valid TokenRequestDto dto) {
        final JwtResponseDto body = jwtAuthFacade.authenticateAndGenerateToken(dto);
        return ResponseEntity.ok(body);
    }
}
