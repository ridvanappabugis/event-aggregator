package com.ridvan.eventaggregator.services;

import com.ridvan.eventaggregator.model.vehicle.VehicleStatistic;
import com.ridvan.eventaggregator.store.StatisticDataStore;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

import static java.lang.Double.max;

@Service
public class StatisticService {
    private final StatisticDataStore dataStore;

    public StatisticService(final StatisticDataStore dataStore) {
        this.dataStore = dataStore;
    }

    public VehicleStatistic persistStatistic(final VehicleStatistic newStatistic) {
        final UUID id = newStatistic.getId();

        final Optional<VehicleStatistic> oldStatistic = findStatistic(id);

        if (!oldStatistic.isPresent()) {
            return dataStore.create(newStatistic);
        }

        return dataStore.update(id, merge(oldStatistic.get(), newStatistic));
    }

    public Optional<VehicleStatistic> findStatistic(final UUID id) {
        return dataStore.find(id);
    }

    public VehicleStatistic merge(final VehicleStatistic oldStatistic, final VehicleStatistic newStatistic) {
        final VehicleStatistic mergedStatistic = new VehicleStatistic(oldStatistic.getId());

        // This assumes telemetry will arrive in order.
        mergedStatistic.setLastMessageTimestamp(newStatistic.getLastMessageTimestamp());
        mergedStatistic.setNumberOfCharges(oldStatistic.getNumberOfCharges() + newStatistic.getNumberOfCharges());
        mergedStatistic.setMaximumSpeed(max(oldStatistic.getMaximumSpeed(), newStatistic.getMaximumSpeed()));
        mergedStatistic.setAverageSpeed(newStatistic.getAverageSpeed());
        mergedStatistic.setCurrentState(newStatistic.getCurrentState());

        return mergedStatistic;
    }
}
