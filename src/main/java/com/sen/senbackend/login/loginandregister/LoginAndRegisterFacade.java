package com.sen.senbackend.login.loginandregister;

import com.sen.senbackend.login.loginandregister.dto.UserRequestDto;
import com.sen.senbackend.login.loginandregister.dto.UserResponseDto;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Log4j2
@AllArgsConstructor
@Component
public class LoginAndRegisterFacade {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final UserUpdater userUpdater;

    public UserResponseDto register(UserRequestDto requestDto) {
        final User user = userMapper.mapToUser(requestDto);
        final User saved = userRepository.save(user);
        return userMapper.mapToUserResponseDto(saved);
    }

    public UserResponseDto findByUsername(String username) {

        final User user = userRepository.findByLogin(username)
                .orElseThrow(() -> new UsernameNotFoundException("User: " + username + " not found"));
        log.info("User found: {}", user);
        return userMapper.mapToUserResponseDto(user);
    }

    public UserResponseDto updateByLogin(String login, UserRequestDto requestDto) {
        return userUpdater.updateByLogin(login, requestDto);
    }

    public UserResponseDto deleteUser(final String login) {
        final User deleted = userRepository.findByLogin(login)
                .orElseThrow(() -> new UsernameNotFoundException("User: " + login + " not found"));
        userRepository.deleteByLogin(deleted.getLogin());
        return userMapper.mapToUserResponseDto(deleted);
    }
}