package com.ridvan.eventaggregator.aggregation.engine;

import com.ridvan.eventaggregator.model.vehicle.VehicleSignal;
import com.ridvan.eventaggregator.model.vehicle.VehicleStatistic;
import com.ridvan.eventaggregator.model.vehicle.VehicleTelemetry;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

public class TelemetryAggregator implements Aggregator<VehicleTelemetry, VehicleStatistic> {
    private final AtomicReference<UUID> lockId;

    private final List<VehicleTelemetry> telemetries;

    public TelemetryAggregator() {
        this.lockId = new AtomicReference<>();
        this.telemetries = new ArrayList<>();
    }

    @Override
    public UUID getLockId() {
        return this.lockId.get();
    }

    @Override
    public synchronized void lock(final UUID id) {
        if (lockId.get() != null) {
            throw new IllegalStateException("Tried to lock, but lock is already present.");
        }

        this.lockId.set(id);
    }

    @Override
    public void add(final VehicleTelemetry telemetry) {
        this.telemetries.add(telemetry);
    }

    @Override
    public VehicleStatistic aggregate() {
        // Initialise new aggregated statistic for this batch
        final VehicleStatistic vehicleStatistic = new VehicleStatistic(this.lockId.get());

        // Aggregate average speed, if no avg then NaN
        vehicleStatistic.setAverageSpeed(
                extractTelemetries(VehicleSignal.CURRENT_SPEED).stream().mapToDouble(d -> d).average().orElse(Double.NaN)
        );

        // Maximum speed of batch
        vehicleStatistic.setMaximumSpeed(
                extractTelemetries(VehicleSignal.CURRENT_SPEED).stream().mapToDouble(d -> d).max().orElse(Double.NaN)
        );

        // Sum charges
        vehicleStatistic.setNumberOfCharges(
                (int) extractTelemetries(VehicleSignal.IS_CHARGING).stream().mapToDouble(d -> d).sum()
        );

        // Max recorded at
        vehicleStatistic.setLastMessageTimestamp(
                this.telemetries.stream().map(VehicleTelemetry::getRecordedAt).max(Long::compareTo).get()
        );

        return vehicleStatistic;
    }

    public synchronized void release() {
        this.lockId.set(null);
        this.telemetries.clear();
    }

    private List<Double> extractTelemetries(final VehicleSignal telemetryType) {
        return this.telemetries.stream()
                .map(t -> t.getSignalValues().get(telemetryType))
                .collect(Collectors.toList());
    }
}
