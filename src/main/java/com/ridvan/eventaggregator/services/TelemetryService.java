package com.ridvan.eventaggregator.services;

import com.ridvan.eventaggregator.model.vehicle.VehicleTelemetry;
import com.ridvan.eventaggregator.store.TelemetryDataStore;
import org.springframework.stereotype.Service;


@Service
public class TelemetryService {
    private final TelemetryDataStore dataStore;

    public TelemetryService(final TelemetryDataStore dataStore) {
        this.dataStore = dataStore;
    }

    public void persistTelemetry(final VehicleTelemetry vehicleTelemetry) {
        this.dataStore.create(vehicleTelemetry);
    }
}
