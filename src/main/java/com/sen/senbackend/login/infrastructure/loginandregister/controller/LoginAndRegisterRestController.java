package com.sen.senbackend.login.infrastructure.loginandregister.controller;


import com.sen.senbackend.login.infrastructure.loginandregister.controller.dto.RegisterRequestDto;
import com.sen.senbackend.login.infrastructure.loginandregister.controller.dto.RegisterResponseDto;
import com.sen.senbackend.login.loginandregister.LoginAndRegisterFacade;
import com.sen.senbackend.login.loginandregister.dto.UserResponseDto;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Log4j2
@AllArgsConstructor
@RequestMapping("/api")
public class LoginAndRegisterRestController {

    private final LoginAndRegisterFacade loginAndRegisterFacade;
    private final LoginAndRegisterMapper mapper;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<RegisterResponseDto> registerUser(@RequestBody @Valid RegisterRequestDto registerRequestDto) {
        final UserResponseDto userResponseDto = loginAndRegisterFacade.register(mapper.fromReqisterRequestDto(registerRequestDto));
        final RegisterResponseDto registered = mapper.fromUserResponseDto(userResponseDto, "REGISTERED");
        return ResponseEntity.status(HttpStatus.CREATED).body(registered);
    }

    @GetMapping("/find/{login}")
    public ResponseEntity<UserResponseDto> findUser(@PathVariable String login) {
        final UserResponseDto byUsername = loginAndRegisterFacade.findByUsername(login);
        return ResponseEntity.ok(byUsername);
    }

    @PutMapping("/update/{login}")
    public ResponseEntity<UserResponseDto> updateUser(@PathVariable String login, @RequestBody @Valid RegisterRequestDto registerRequestDto) {
        final UserResponseDto body = loginAndRegisterFacade.updateByLogin(login, mapper.fromReqisterRequestDto(registerRequestDto));
        return ResponseEntity.ok(body);
    }

    @DeleteMapping("/delete/{login}")
    public ResponseEntity<UserResponseDto> deleteUser(@PathVariable String login) {
        return ResponseEntity.ok(loginAndRegisterFacade.deleteUser(login));
    }
}