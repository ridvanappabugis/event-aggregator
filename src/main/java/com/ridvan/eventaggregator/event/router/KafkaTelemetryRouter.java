package com.ridvan.eventaggregator.event.router;

import com.ridvan.eventaggregator.aggregation.engine.TelemetryAggregationEngine;
import com.ridvan.eventaggregator.model.vehicle.VehicleTelemetry;
import com.ridvan.eventaggregator.services.TelemetryService;
import com.ridvan.eventaggregator.services.TelemetryValidationService;

import java.util.UUID;

/**
 * Routes telemetry messages to the respective event handlers
 */
public class KafkaTelemetryRouter implements EventRouter {
    private final TelemetryAggregationEngine aggregationHandler;
    private final TelemetryService persistenceHandler;
    private final TelemetryValidationService validationHandler;

    public KafkaTelemetryRouter(final TelemetryAggregationEngine aggregationHandler,
                                final TelemetryService persistenceHandler,
                                final TelemetryValidationService validationHandler) {
        this.aggregationHandler = aggregationHandler;
        this.persistenceHandler = persistenceHandler;
        this.validationHandler = validationHandler;
    }

    @Override
    public void route(final UUID routingKey, final Object event) {
        final VehicleTelemetry telemetry = (VehicleTelemetry) event;
        // Validate
        this.validationHandler.validateTelemetry(telemetry);
        // Aggregate
        this.aggregationHandler.aggregateTelemetry(routingKey, telemetry);
        // Persist
        this.persistenceHandler.persistTelemetry(telemetry);
    }
}
