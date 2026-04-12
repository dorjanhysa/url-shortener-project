package com.dorjan.urlshortener.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class BusinessException extends RuntimeException {

    private final String errorCode;
    private final HttpStatus httpStatus;

    public BusinessException(String message, String errorCode, HttpStatus httpStatus) {
        super(message);
        this.errorCode = errorCode;
        this.httpStatus = httpStatus;
    }

    public static final String URL_NOT_FOUND = "URL_NOT_FOUND";
    public static final String URL_EXPIRED = "URL_EXPIRED";
    public static final String USER_ALREADY_EXISTS = "USER_ALREADY_EXISTS";
    public static final String INVALID_CREDENTIALS = "INVALID_CREDENTIALS";

    public static BusinessException urlNotFound() {
        return new BusinessException(
                "URL not found",
                URL_NOT_FOUND,
                HttpStatus.NOT_FOUND);
    }

    public static BusinessException urlExpired() {
        return new BusinessException(
                "URL has expired",
                URL_EXPIRED,
                HttpStatus.GONE);
    }

    public static BusinessException userAlreadyExists() {
        return new BusinessException(
                "User already exists",
                USER_ALREADY_EXISTS,
                HttpStatus.CONFLICT);
    }

    public static BusinessException invalidCredentials() {
        return new BusinessException(
                "Invalid credentials",
                INVALID_CREDENTIALS,
                HttpStatus.UNAUTHORIZED);
    }
}
