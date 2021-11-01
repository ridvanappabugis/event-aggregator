package com.ridvan.eventaggregator.services;

import com.ridvan.eventaggregator.model.vehicle.VehicleSignal;
import com.ridvan.eventaggregator.model.vehicle.VehicleTelemetry;
import com.ridvan.eventaggregator.rest.exceptions.ValidationException;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class TelemetryValidationService {

    /**
     * Validates a telemetry in the context of a single telemetry datapoint.
     */
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
                        throw new ValidationException("IS_CHARGING Telemetry signal invalid, discarding telemetry.");
                    }
                    break;
            }
        }

        final Double currentSpeed = telemetry.getSignalValues().get(VehicleSignal.CURRENT_SPEED);
        final Double isCharging = telemetry.getSignalValues().get(VehicleSignal.IS_CHARGING);

        if (isCharging != null && isCharging == 1d && currentSpeed != null && currentSpeed != 0d) {
            throw new ValidationException("Charging and driving should not be possible, discarding telemetry.");
        }

        final Double odometer = telemetry.getSignalValues().get(VehicleSignal.ODOMETER);
        final Double drivingTime = telemetry.getSignalValues().get(VehicleSignal.DRIVING_TIME);

        if (odometer != null && drivingTime != null) {
            if (odometer != 0d && drivingTime == 0d || odometer == 0d && drivingTime != 0d) {
                throw new ValidationException("Invalid odometer + driving time combination both need to be > 0, discarding telemetry.");
            }
        }

    }
}
