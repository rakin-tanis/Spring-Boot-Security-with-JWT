package com.wakeupneo.security.api;

import com.wakeupneo.security.dto.HttpErrorResponse;
import com.wakeupneo.security.exception.AlreadyActivatedAccountException;
import com.wakeupneo.security.exception.EntityExistsException;
import com.wakeupneo.security.exception.EntityNotFoundException;
import com.wakeupneo.security.exception.InvalidParameterException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;

import static org.springframework.http.HttpStatus.*;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.MediaType.APPLICATION_JSON;

@Slf4j
@Component
@ControllerAdvice
public class RestControllerAdvice {

    private static final String COLON = " : ";
    private static final String ERROR_MESSAGE = "Fatal error: ";
    private static final int SB_INITIAL_CAPACITY = 128;

    @ExceptionHandler(Exception.class)
    @ResponseStatus(INTERNAL_SERVER_ERROR)
    public ResponseEntity<HttpErrorResponse> handleException(Exception exception) {
        String uuid = UUID.randomUUID().toString();
        log.error(uuid + COLON + getMessage(exception), exception);
        return createHttpErrorResponse(INTERNAL_SERVER_ERROR, ERROR_MESSAGE + uuid);
    }

    @ExceptionHandler(EntityExistsException.class)
    @ResponseStatus(CONFLICT)
    public ResponseEntity<HttpErrorResponse> handleEntityExistsException(EntityExistsException exception) {
        String uuid = UUID.randomUUID().toString();
        log.error(uuid + COLON + getMessage(exception), exception);
        return createHttpErrorResponse(CONFLICT, getMessage(exception));
    }

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(NOT_FOUND)
    public ResponseEntity<HttpErrorResponse> handleEntityNotFoundException(EntityNotFoundException exception) {
        String uuid = UUID.randomUUID().toString();
        log.error(uuid + COLON + getMessage(exception), exception);
        return createHttpErrorResponse(NOT_FOUND, getMessage(exception));
    }

    @ExceptionHandler(InvalidParameterException.class)
    @ResponseStatus(BAD_REQUEST)
    public ResponseEntity<HttpErrorResponse> handleInvalidParameterException(InvalidParameterException exception) {
        String uuid = UUID.randomUUID().toString();
        log.error(uuid + COLON + getMessage(exception), exception);
        return createHttpErrorResponse(BAD_REQUEST, getMessage(exception));
    }

    @ExceptionHandler(AuthenticationException.class)
    @ResponseStatus(UNAUTHORIZED)
    public ResponseEntity<HttpErrorResponse> handleAuthenticationException(AuthenticationException exception) {
        String uuid = UUID.randomUUID().toString();
        log.error(uuid + COLON + getMessage(exception), exception);
        return createHttpErrorResponse(UNAUTHORIZED, getMessage(exception));
    }

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(FORBIDDEN)
    public ResponseEntity<HttpErrorResponse> handleAccessDeniedException(AccessDeniedException exception) {
        String uuid = UUID.randomUUID().toString();
        log.error(uuid + COLON + getMessage(exception), exception);
        return createHttpErrorResponse(FORBIDDEN, getMessage(exception));
    }

    @ExceptionHandler(AlreadyActivatedAccountException.class)
    @ResponseStatus(NOT_ACCEPTABLE)
    public ResponseEntity<HttpErrorResponse> handleAlreadyActivatedAccountException(AlreadyActivatedAccountException exception) {
        String uuid = UUID.randomUUID().toString();
        log.error(uuid + COLON + getMessage(exception), exception);
        return createHttpErrorResponse(NOT_ACCEPTABLE, getMessage(exception));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(BAD_REQUEST)
    public ResponseEntity<HttpErrorResponse> handleArgumentException(MethodArgumentNotValidException exception) {
        String uuid = UUID.randomUUID().toString();
        log.error(uuid + COLON + getMessage(exception), exception);

        String message = "Required parameters must be provided. Validation failed. %d error(s) -> %s";

        StringBuilder validationMessages = new StringBuilder(SB_INITIAL_CAPACITY);
        AtomicInteger errorCount = new AtomicInteger();
        if (exception.getBindingResult().getFieldErrors().isEmpty()) {
            exception.getBindingResult().getAllErrors().forEach(env -> {
                validationMessages.append("[")
                        .append(env.getDefaultMessage())
                        .append("]");
                errorCount.getAndIncrement();
            });
        } else {
            exception.getBindingResult().getFieldErrors().forEach(env -> {
                validationMessages.append("[")
                        .append(env.getField())
                        .append("]:[")
                        .append(env.getDefaultMessage())
                        .append("]");
                errorCount.getAndIncrement();
            });
        }

        return createHttpErrorResponse(BAD_REQUEST, String.format(message, errorCount.get(), validationMessages));
    }

    private String getMessage(Exception exception) {
        return StringUtils.defaultString(exception.getMessage());
    }

    private ResponseEntity<HttpErrorResponse> createHttpErrorResponse(HttpStatus httpStatus, String message) {
        return ResponseEntity
                .status(httpStatus)
                .contentType(APPLICATION_JSON)
                .body(HttpErrorResponse.builder()
                        .statusCode(httpStatus.value())
                        .status(httpStatus)
                        .reason(httpStatus.getReasonPhrase())
                        .message(message)
                        .build());
    }
}
