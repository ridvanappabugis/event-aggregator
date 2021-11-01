package com.ridvan.eventaggregator.store.impl;

import com.ridvan.eventaggregator.model.vehicle.VehicleStatistic;
import com.ridvan.eventaggregator.store.StatisticDataStore;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class InMemoryStatisticDataStore implements StatisticDataStore {
    private final Map<UUID, VehicleStatistic> memory;

    InMemoryStatisticDataStore(final Map<UUID, VehicleStatistic> memory) {
        Objects.requireNonNull(memory, "Memory map must not be null.");

        this.memory = memory;
    }

    public InMemoryStatisticDataStore() {
        this(new ConcurrentHashMap<>());
    }

    @Override
    public Optional<VehicleStatistic> find(final UUID id) {
        Objects.requireNonNull(id);

        return Optional.ofNullable(this.memory.get(id));
    }

    @Override
    public VehicleStatistic create(final VehicleStatistic statistic) {
        Objects.requireNonNull(statistic);

        if (this.memory.containsKey(statistic.getId())) {
            throw new IllegalStateException("Statistic with id: " + statistic.getId() + " is already present.");
        }

        return this.memory.put(statistic.getId(), statistic);
    }

    @Override
    public VehicleStatistic update(final UUID id, final VehicleStatistic statistic) {
        Objects.requireNonNull(id);
        if (!this.memory.containsKey(id)) {
            throw new IllegalStateException("Statistic with id: " + id + " not found.");
        }

        return this.memory.put(id, statistic);
    }
}
