package com.sen.senbackend.login.infrastructure.loginandregister.controller;


import com.sen.senbackend.login.infrastructure.loginandregister.controller.dto.RegisterRequestDto;
import com.sen.senbackend.login.infrastructure.loginandregister.controller.dto.RegisterResponseDto;
import com.sen.senbackend.login.loginandregister.dto.UserRequestDto;
import com.sen.senbackend.login.loginandregister.dto.UserResponseDto;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
class LoginAndRegisterMapper {
    private final PasswordEncoder passwordEncoder;

    UserRequestDto fromReqisterRequestDto(RegisterRequestDto dto) {
        return UserRequestDto.builder()
                .login(dto.login())
                .password(passwordEncoder.encode(dto.password()))
                .build();
    }

    RegisterResponseDto fromUserResponseDto(UserResponseDto dto, String message) {
        return RegisterResponseDto.builder()
                .login(dto.login())
                .message(message)
                .build();
    }

}
