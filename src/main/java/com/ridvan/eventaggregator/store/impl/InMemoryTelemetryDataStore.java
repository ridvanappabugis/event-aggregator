package com.ridvan.eventaggregator.store.impl;

import com.ridvan.eventaggregator.model.vehicle.VehicleTelemetry;
import com.ridvan.eventaggregator.store.TelemetryDataStore;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryTelemetryDataStore implements TelemetryDataStore {
    private final Map<UUID, List<VehicleTelemetry>> memory;

    InMemoryTelemetryDataStore(final Map<UUID, List<VehicleTelemetry>> memory) {
        Objects.requireNonNull(memory, "Memory map must not be null.");

        this.memory = memory;
    }

    public InMemoryTelemetryDataStore() {
        this(new ConcurrentHashMap<>());
    }

    @Override
    public void create(final VehicleTelemetry telemetry) {
        if (!this.memory.containsKey(telemetry.getId())) {
            this.memory.put(telemetry.getId(), new ArrayList<>());
        }

        this.memory.get(telemetry.getId()).add(telemetry);
    }
}
