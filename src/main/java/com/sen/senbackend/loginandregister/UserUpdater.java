package com.sen.senbackend.loginandregister;

import com.sen.senbackend.loginandregister.dto.UserRequestDto;
import com.sen.senbackend.loginandregister.dto.UserResponseDto;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Log4j2
@Service
@AllArgsConstructor
@Transactional
class UserUpdater {
    private final UserRepository userRepository;
    private final UserMapper userMapper;


    UserResponseDto updateByLogin(String login, UserRequestDto requestDto) {
        final User user = userRepository.findByLogin(login)
                .orElseThrow(() -> new UsernameNotFoundException("User" + login + " not found"));
        log.info("User found: {}", user);
        final User updated = userMapper.mapToUser(requestDto);

        userRepository.updateLoginAndPasswordById(requestDto.login(), requestDto.password(), user.getId());
        log.info("User updated: {}", updated);
        return userMapper.mapToUserResponseDto(updated);
    }
}
