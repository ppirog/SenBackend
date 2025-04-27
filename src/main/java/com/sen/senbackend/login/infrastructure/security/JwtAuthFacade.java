package com.sen.senbackend.login.infrastructure.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.sen.senbackend.login.infrastructure.security.dto.JwtResponseDto;
import com.sen.senbackend.login.infrastructure.security.dto.TokenRequestDto;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;

import java.time.*;

@Log4j2
@Component
@AllArgsConstructor
public class JwtAuthFacade {

    private final AuthenticationManager authenticatorManager;
    private final Clock clock;
    private final JwtConfigProperties properties;

    public JwtResponseDto authenticateAndGenerateToken(TokenRequestDto tokenRequestDto){
        Authentication authenticate = authenticatorManager.authenticate(
                new UsernamePasswordAuthenticationToken(tokenRequestDto.login(), tokenRequestDto.password())
        );
        final User principal = (User) authenticate.getPrincipal();
        String token = createToken(principal);
        String login = principal.getUsername();
        return JwtResponseDto.builder()
                .token(token)
                .login(login)
                .build();
    }

    private String createToken(final User user) {
        String secretKey =  properties.secret();
        Algorithm algorithm = Algorithm.HMAC256(secretKey);

        Instant now = LocalDateTime.now(clock).toInstant(ZoneOffset.UTC);
        Instant expireAt = now.plus(Duration.ofHours(2));
        String issuer = "Sen";

        return JWT.create()
                .withSubject(user.getUsername())
                .withIssuedAt(now)
                .withExpiresAt(expireAt)
                .withIssuer(issuer)
                .sign(algorithm);
    }

}
