package com.ridvan.eventaggregator.model.vehicle;

import java.util.Map;
import java.util.UUID;

public class VehicleTelemetry {
    private UUID id;
    private long recordedAt;
    private Map<VehicleSignal, Double> signalValues;

    public VehicleTelemetry() {
    }

    public VehicleTelemetry(final UUID id,
                            final long recordedAt,
                            final Map<VehicleSignal, Double> signalValues) {
        this.id = id;
        this.recordedAt = recordedAt;
        this.signalValues = signalValues;
    }

    public UUID getId() {
        return id;
    }

    public long getRecordedAt() {
        return recordedAt;
    }

    public Map<VehicleSignal, Double> getSignalValues() {
        return signalValues;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public void setRecordedAt(long recordedAt) {
        this.recordedAt = recordedAt;
    }

    public void setSignalValues(Map<VehicleSignal, Double> signalValues) {
        this.signalValues = signalValues;
    }
}
