package com.ridvan.eventaggregator.services;

import com.ridvan.eventaggregator.event.router.TelemetryRouter;
import com.ridvan.eventaggregator.model.vehicle.VehicleTelemetry;
import com.ridvan.eventaggregator.util.JSONConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * Kafka receiver service.
 */
@Service
public class KafkaReceiverService {
    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaReceiverService.class);

    private final TelemetryRouter telemetryRouter;

    /**
     * Instantiates new Kafka Receiver Service
     */
    public KafkaReceiverService(@Qualifier("telemetryRouter") final TelemetryRouter telemetryRouter) {
        this.telemetryRouter = Objects.requireNonNull(telemetryRouter, "kafkaEventRouter must not be null.");
    }

    /**
     * The output endpoint. Receives Geoenrichment responses and routes them to the appropriate steps.
     */
    @KafkaListener(topics = "${spring.kafka.template.default-topic}")
    public void handleOutput(@Payload final String rawResponse, final Acknowledgment ack) {
        LOGGER.info("[KAFKA-IN]: Received telemetry message: {} ", rawResponse);

        final VehicleTelemetry vehicleTelemetry = JSONConverter.fromJSON(rawResponse, VehicleTelemetry.class);

        this.telemetryRouter.route(vehicleTelemetry.getId(), vehicleTelemetry);
    }

}
