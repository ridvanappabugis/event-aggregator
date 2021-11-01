package com.ridvan.eventaggregator.rest.controllers;

import com.ridvan.eventaggregator.event.router.TelemetryRouter;
import com.ridvan.eventaggregator.model.vehicle.VehicleTelemetry;
import com.ridvan.eventaggregator.util.JSONConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequestMapping("/telemetry")
public class VehicleTelemetryController {
    private static final Logger LOGGER = LoggerFactory.getLogger(VehicleTelemetryController.class);

    private final TelemetryRouter telemetryRouter;

    @Autowired
    public VehicleTelemetryController(final TelemetryRouter telemetryRouter) {
        this.telemetryRouter = telemetryRouter;
    }

    /**
     * POST vehicle telemetry to the aggregation engine.
     * Alternative way to publish events, to test aggregation.
     */
    @PostMapping(consumes = "application/json")
    public String post(@RequestBody final List<VehicleTelemetry> telemetries) {

        for (final VehicleTelemetry telemetry: telemetries) {
            LOGGER.info("[HTTP-IN]: Received telemetry message: {} ", JSONConverter.toJSON(telemetry));

            telemetryRouter.route(telemetry.getId(), telemetry);
        }

        return "Successfully published telemetries";
    }
}
