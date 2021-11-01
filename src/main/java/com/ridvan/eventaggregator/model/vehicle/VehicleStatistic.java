package com.ridvan.eventaggregator.model.vehicle;

import java.util.UUID;

/**
 * The Vehicle Telemetry model class.
 */
public class VehicleStatistic {
    private static final Double ZERO_D = 0.0;
    private final UUID id;
    private Double averageSpeed;
    private Double maximumSpeed;
    private long lastMessageTimestamp;
    private Integer numberOfCharges;
    private VehicleState currentState;

    public VehicleStatistic(final UUID id) {
        this(id, ZERO_D, ZERO_D, 0L, 0, VehicleState.UNKNOWN);
    }

    public VehicleStatistic(final UUID id,
                            final Double averageSpeed,
                            final Double maximumSpeed,
                            final long lastMessageTimestamp,
                            final Integer numberOfCharges,
                            final VehicleState currentState) {
        this.id = id;
        this.averageSpeed = averageSpeed;
        this.maximumSpeed = maximumSpeed;
        this.lastMessageTimestamp = lastMessageTimestamp;
        this.numberOfCharges = numberOfCharges;
        this.currentState = currentState;
    }

    public UUID getId() {
        return id;
    }

    public Double getAverageSpeed() {
        return averageSpeed;
    }

    public Double getMaximumSpeed() {
        return maximumSpeed;
    }

    public long getLastMessageTimestamp() {
        return lastMessageTimestamp;
    }

    public Integer getNumberOfCharges() {
        return numberOfCharges;
    }

    public VehicleState getCurrentState() {
        return currentState;
    }

    public void setAverageSpeed(final Double averageSpeed) {
        this.averageSpeed = averageSpeed;
    }

    public void setMaximumSpeed(final Double maximumSpeed) {
        this.maximumSpeed = maximumSpeed;
    }

    public void setLastMessageTimestamp(final long lastMessageTimestamp) {
        this.lastMessageTimestamp = lastMessageTimestamp;
    }

    public void setNumberOfCharges(final Integer numberOfCharges) {
        this.numberOfCharges = numberOfCharges;
    }

    public void setCurrentState(final VehicleState currentState) {
        this.currentState = currentState;
    }
}
