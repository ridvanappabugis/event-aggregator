package com.ridvan.eventaggregator.store;

import com.ridvan.eventaggregator.model.vehicle.VehicleTelemetry;

/**
 * Vehicle Telemetry Data Store contract.
 * Currently, only create is implemented as this serves as a data warehouse contract.
 */
public interface TelemetryDataStore {

    void create(final VehicleTelemetry telemetry);
}
