package ru.practicum.stat.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorDto handleBadRequest(final MethodArgumentNotValidException e) {
        log.error("{} : {}", e.getClass().getSimpleName(), e.getMessage());
        return e.getBindingResult().getAllErrors().stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .map(message -> new ErrorDto(400, message))
                .findFirst()
                .orElse(new ErrorDto(400, "Invalid data"));
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorDto handleInternalError(final ValidException e) {
        log.error("{} : {}", e.getClass().getSimpleName(), e.getMessage());
        return new ErrorDto(400, e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorDto handleInternalError(final RuntimeException e) {
        log.error("{} : {}", e.getClass().getSimpleName(), e.getMessage());
        return new ErrorDto(500, e.getMessage());
    }
}
