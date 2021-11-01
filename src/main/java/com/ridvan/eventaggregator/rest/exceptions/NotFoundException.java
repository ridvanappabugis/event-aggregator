package com.ridvan.eventaggregator.rest.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.Objects;

@ResponseStatus(value= HttpStatus.NOT_FOUND, reason="Not found")
public class NotFoundException extends RuntimeException {
    private final String vehicleId;

    public NotFoundException(final String vehicleId) {
        this.vehicleId = Objects.requireNonNull(vehicleId, "vehicleId cannot be null");
    }

    public String vehicleId() {
        return this.vehicleId;
    }
}
