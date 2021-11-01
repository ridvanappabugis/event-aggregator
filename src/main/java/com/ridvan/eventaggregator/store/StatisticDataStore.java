package com.ridvan.eventaggregator.store;

import com.ridvan.eventaggregator.model.vehicle.VehicleStatistic;

import java.util.Optional;
import java.util.UUID;

/**
 * Vehicle Statistic Data Store contract.
 * <p>
 * Contains basic CRU operations for statistics.
 */
public interface StatisticDataStore {

    Optional<VehicleStatistic> find(final UUID id);

    VehicleStatistic create(final VehicleStatistic statistic);

    VehicleStatistic update(final UUID id, final VehicleStatistic statistic);
}
