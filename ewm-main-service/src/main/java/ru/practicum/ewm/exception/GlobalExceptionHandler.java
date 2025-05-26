package ru.practicum.ewm.exception;

import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiErrorDto handleBadRequest(final MethodArgumentNotValidException e) {
        log.error("{} : {}", e.getClass().getSimpleName(), e.getMessage());
        return e.getBindingResult().getAllErrors().stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .map(message -> new ApiErrorDto("BAD_REQUEST", "Incorrectly made request.", message, LocalDateTime.now()))
                .findFirst()
                .orElse(new ApiErrorDto("BAD_REQUEST", "Incorrectly made request.", e.getMessage(), LocalDateTime.now()));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiErrorDto handleBadRequest(final ConstraintViolationException e) {
        log.error("{} : {}", e.getClass().getSimpleName(), e.getMessage());
        return new ApiErrorDto("BAD_REQUEST", "Incorrectly made request.", e.getMessage(), LocalDateTime.now());
    }

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiErrorDto handleNotFound(final NotFoundException e) {
        log.error("{} : {}", e.getClass().getSimpleName(), e.getMessage());
        return new ApiErrorDto("NOT_FOUND", "The required object was not found.", e.getMessage(), LocalDateTime.now());
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ApiErrorDto handleConflictException(final DataIntegrityViolationException e) {
        log.error("{} : {}", e.getClass().getSimpleName(), e.getMessage());
        return new ApiErrorDto("CONFLICT", "Integrity constraint has been violated.", e.getMessage(), LocalDateTime.now());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiErrorDto handleInternalError(final RuntimeException e) {
        log.error("{} : {}", e.getClass().getSimpleName(), e.getMessage());
        return new ApiErrorDto("INTERNAL_SERVER_ERROR", "Oops...", e.getMessage(), LocalDateTime.now());
    }
}
