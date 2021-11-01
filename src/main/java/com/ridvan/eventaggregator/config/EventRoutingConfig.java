package com.ridvan.eventaggregator.config;

import com.ridvan.eventaggregator.aggregation.engine.TelemetryAggregationEngine;
import com.ridvan.eventaggregator.event.router.TelemetryRouter;
import com.ridvan.eventaggregator.services.TelemetryService;
import com.ridvan.eventaggregator.services.TelemetryValidationService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EventRoutingConfig {

    /**
     * A bridge between Kafka listener and the Aggregation Engine.
     */
    @Bean
    TelemetryRouter telemetryRouter(final TelemetryAggregationEngine engine,
                                    final TelemetryService telemetryService,
                                    final TelemetryValidationService validationService) {
        return new TelemetryRouter(engine, telemetryService, validationService);
    }
}
