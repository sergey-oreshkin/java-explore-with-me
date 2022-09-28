package ru.practicum.explorewithme.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import ru.practicum.explorewithme.exception.dto.ErrorDto;

import java.time.LocalDateTime;
import java.util.Collections;

@RestControllerAdvice
@Slf4j
public class ControllerExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorDto handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        ErrorDto errorDto = ErrorDto.builder()
                .errors(Collections.emptyList())
                .message(ex.getFieldError().getDefaultMessage())
                .reason(String.valueOf(ex.getFieldError().getRejectedValue()))
                .status(HttpStatus.BAD_REQUEST)
                .timestamp(LocalDateTime.now())
                .build();
        log.error(errorDto.toString());
        return errorDto;
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorDto handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException ex) {
        ErrorDto errorDto = ErrorDto.builder()
                .errors(Collections.emptyList())
                .message(ex.getMessage())
                .reason(ex.getErrorCode())
                .status(HttpStatus.BAD_REQUEST)
                .timestamp(LocalDateTime.now())
                .build();
        log.error(errorDto.toString());
return errorDto;
    }

    @ExceptionHandler(ConflictException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorDto handleConflictException(ConflictException ex) {
        ErrorDto errorDto = ErrorDto.builder()
                .errors(Collections.emptyList())
                .message(ex.getMessage())
                .reason(ex.getRejectedValue())
                .status(HttpStatus.CONFLICT)
                .timestamp(LocalDateTime.now())
                .build();
        log.error(errorDto.toString());
        return errorDto;
    }

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorDto handleNotFoundException(NotFoundException ex) {
        ErrorDto errorDto = ErrorDto.builder()
                .errors(Collections.emptyList())
                .message(ex.getMessage())
                .reason(ex.getRejectedValue())
                .status(HttpStatus.NOT_FOUND)
                .timestamp(LocalDateTime.now())
                .build();
        log.error(errorDto.toString());
        return errorDto;
    }

    @ExceptionHandler(ValidationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorDto handleValidationException(ValidationException ex) {
        ErrorDto errorDto = ErrorDto.builder()
                .errors(Collections.emptyList())
                .message(ex.getMessage())
                .reason(ex.getRejectedValue())
                .status(HttpStatus.BAD_REQUEST)
                .timestamp(LocalDateTime.now())
                .build();
        log.error(errorDto.toString());
        return errorDto;
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorDto handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
        ErrorDto errorDto = ErrorDto.builder()
                .errors(Collections.emptyList())
                .message(ex.getMessage())
                .reason("")
                .status(HttpStatus.BAD_REQUEST)
                .timestamp(LocalDateTime.now())
                .build();
        log.error(errorDto.toString());
        return errorDto;
    }

    @ExceptionHandler(InvalidDataAccessApiUsageException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorDto handleUnknownException(InvalidDataAccessApiUsageException ex) {
        ErrorDto errorDto = ErrorDto.builder()
                .errors(Collections.emptyList())
                .message(ex.getMessage())
                .reason("")
                .status(HttpStatus.BAD_REQUEST)
                .timestamp(LocalDateTime.now())
                .build();
        log.error(errorDto.toString());
        return errorDto;
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorDto handleUnknownException(Exception ex) {
        ErrorDto errorDto =  ErrorDto.builder()
                .errors(Collections.emptyList())
                .message(ex.getMessage())
                .reason("")
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .timestamp(LocalDateTime.now())
                .build();
        log.error(errorDto.toString());
        return errorDto;
    }
}
