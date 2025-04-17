package com.sen.senbackend.login.infrastructure.errorvalidation;

import com.sen.senbackend.exception.GameLogicException;
import com.sen.senbackend.exception.dto.GameLogicErrorResponseDto;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;

@Log4j2
@ControllerAdvice
public class ExceptionsHandlers {

    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.CONFLICT)
    public DuplicateKeyExceptionDto handleDataIntegrityViolationException() {
        final String loginAlreadyExists = "Login already exists";
        log.warn(loginAlreadyExists);
        return DuplicateKeyExceptionDto.builder()
                .message(loginAlreadyExists)
                .build();
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public UsernameNotFoundExceptionDto handleUsernameNotFoundException() {
        final String loginNotFound = "Login not found";
        log.warn(loginNotFound);
        return UsernameNotFoundExceptionDto.builder()
                .message(loginNotFound)
                .build();
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiValidationErrorResponseDto handleMethodArgumentNotValidException(MethodArgumentNotValidException exception) {
        final List<String> errors = exception.getBindingResult().getAllErrors().stream().map(DefaultMessageSourceResolvable::getDefaultMessage).toList();
        log.warn("Validation error{}", errors);
        return ApiValidationErrorResponseDto.builder()
                .errors(errors)
                .status(HttpStatus.BAD_REQUEST)
                .build();
    }

    @ExceptionHandler(GameLogicException.class)
    @ResponseBody
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public GameLogicErrorResponseDto handleGameLogicException(GameLogicException ex) {
        String msg = ex.getMessage();
        log.warn("Game logic error: {}", msg);
        return GameLogicErrorResponseDto.builder()
                .message(msg)
                .build();
    }
}
