package com.ridvan.eventaggregator.rest.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value= HttpStatus.BAD_REQUEST, reason="Not valid")
public class ValidationException extends RuntimeException {

    public ValidationException(final String message) {
        super(message);
    }
}
