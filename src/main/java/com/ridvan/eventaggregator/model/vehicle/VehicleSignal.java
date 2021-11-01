package com.ridvan.eventaggregator.model.vehicle;

public enum VehicleSignal {

    CURRENT_SPEED("currentSpeed"),
    ODOMETER("odometer"),
    DRIVING_TIME("drivingTime"),
    IS_CHARGING("isCharging");

    private final String signalName;

    VehicleSignal(final String signalName) {
        this.signalName = signalName;
    }

    public String getSignalName() {
            return signalName;
        }

}
