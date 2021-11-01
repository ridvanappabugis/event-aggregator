package com.ridvan.eventaggregator.services;

import com.ridvan.eventaggregator.model.vehicle.VehicleSignal;
import com.ridvan.eventaggregator.model.vehicle.VehicleTelemetry;
import com.ridvan.eventaggregator.rest.exceptions.ValidationException;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class TelemetryValidationService {

    public void validateTelemetry(final VehicleTelemetry telemetry) {
        if (telemetry.getRecordedAt() <= 0) {
            throw new ValidationException("Telemetry recordedAt property is invalid, discarding telemetry.");
        }

        if (telemetry.getSignalValues().isEmpty()) {
            throw new ValidationException("Telemetry signals are empty, discarding telemetry.");
        }

        for (final Map.Entry<VehicleSignal, Double> entry: telemetry.getSignalValues().entrySet()) {
            final Double signalValue = entry.getValue();

            switch (entry.getKey()) {

                case CURRENT_SPEED:
                case ODOMETER:
                case DRIVING_TIME:
                    if (signalValue.isInfinite() || signalValue.isNaN() || signalValue < 0d) {
                        throw new ValidationException(entry.getKey().getSignalName() + " Telemetry signal invalid, discarding telemetry.");
                    }
                    break;
                case IS_CHARGING:
                    if (signalValue != 1d && signalValue != 0d) {
                        throw new ValidationException("IS_CHARING Telemetry signal invalid, discarding telemetry.");
                    }
                    break;
            }
        }
    }
}
