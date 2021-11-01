package com.ridvan.eventaggregator.aggregation.engine;

import com.ridvan.eventaggregator.model.vehicle.VehicleSignal;
import com.ridvan.eventaggregator.model.vehicle.VehicleState;
import com.ridvan.eventaggregator.model.vehicle.VehicleStatistic;
import com.ridvan.eventaggregator.model.vehicle.VehicleTelemetry;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.TreeSet;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

public class TelemetryAggregator implements Aggregator<VehicleTelemetry, VehicleStatistic> {
    private final AtomicReference<UUID> lockId;

    private final TreeSet<VehicleTelemetry> telemetries;

    public TelemetryAggregator() {
        this.lockId = new AtomicReference<>();
        // Sorted telemetries
        this.telemetries = new TreeSet<>(Comparator.comparingDouble(VehicleTelemetry::getRecordedAt));
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

    /**
     * Aggregates the accumulated batch of telemetries into a vehicle statistic.
     * @return accumulated vehicle statistic for this batch.
     */
    @Override
    public VehicleStatistic aggregate() {
        // Initialise new aggregated statistic for this batch
        final VehicleStatistic vehicleStatistic = new VehicleStatistic(this.lockId.get());

        // Average speed can be calculated in 2 ways:

        // 1. Last entry with both odometer and driving time => avg = odometer/driving time (preferred method in this project)
        // This approach essentially takes in account calculation over a single telemetry context.

        // 2. Interpolation with factors in calculation being current_speed & odometer & driving_time
        // This will require interpolating over the batch to get the approximate values needed to calculate the avg speed.
        // Second is more informative as it will amend the problem of not all signals being present.
        // But it will require a more extensive implementation. TODO at a later date.

        final VehicleTelemetry lastAvgTelemetry = this.telemetries.stream()
                .filter(
                        t -> t.getSignalValues().containsKey(VehicleSignal.DRIVING_TIME) &&
                                t.getSignalValues().containsKey(VehicleSignal.ODOMETER)
                )
                .sorted(Comparator.comparingDouble(VehicleTelemetry::getRecordedAt))
                .reduce((first, second) -> second)
                .orElse(null);

        vehicleStatistic.setAverageSpeed(
            lastAvgTelemetry != null ?
                    lastAvgTelemetry.getSignalValues().get(VehicleSignal.ODOMETER)/lastAvgTelemetry.getSignalValues().get(VehicleSignal.DRIVING_TIME) : Double.NaN
        );

        // Maximum speed of batch
        vehicleStatistic.setMaximumSpeed(
                extractTelemetries(VehicleSignal.CURRENT_SPEED).stream().filter(Objects::nonNull).mapToDouble(d -> d).max().orElse(Double.NaN)
        );

        // Sum charges
        vehicleStatistic.setNumberOfCharges(
                (int) extractTelemetries(VehicleSignal.IS_CHARGING).stream().filter(Objects::nonNull).mapToDouble(d -> d).sum()
        );

        // Max recorded at
        vehicleStatistic.setLastMessageTimestamp(this.telemetries.last().getRecordedAt());

        // Set Vehicle state, it is resolved from the last entry of this batch
        vehicleStatistic.setCurrentState(resolveState(this.telemetries.last()));

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

    private VehicleState resolveState(final VehicleTelemetry vehicleTelemetry) {
        VehicleState state = VehicleState.UNKNOWN;

        // At this point we are assured we have valid telemetries, from a business/calculation point of view.

        final Double currentSpeed = vehicleTelemetry.getSignalValues().get(VehicleSignal.CURRENT_SPEED);
        final Double charging = vehicleTelemetry.getSignalValues().get(VehicleSignal.IS_CHARGING);

        if (currentSpeed != null) {
            state = currentSpeed != 0d ? VehicleState.DRIVING : VehicleState.PARKED;
        }

        if (charging != null && (state == VehicleState.PARKED || state == VehicleState.UNKNOWN) && charging == 1d) {
            state = VehicleState.CHARGING;
        }

        return state;
    }
}
