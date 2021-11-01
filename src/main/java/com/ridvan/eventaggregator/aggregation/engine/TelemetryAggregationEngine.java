package com.ridvan.eventaggregator.aggregation.engine;

import com.ridvan.eventaggregator.model.vehicle.VehicleStatistic;
import com.ridvan.eventaggregator.model.vehicle.VehicleTelemetry;
import com.ridvan.eventaggregator.services.StatisticService;
import com.sun.org.apache.xml.internal.utils.ObjectPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;

import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Engine worker. Task of this class is to aggregate incoming telemetry events.
 * Uses a pool of aggregators for aggregation, periodically releasing the locked aggregators
 *          - thus completing their aggregation for the accumulated batch.
 * In case no aggregators are available, it will block event submission until the aggregators are freed.
 */
public class TelemetryAggregationEngine {
    private static final Logger LOGGER = LoggerFactory.getLogger(TelemetryAggregationEngine.class);

    private static final int OFFER_SLEEP_MILLIS = 100;

    private final ObjectPool aggregatorPool;
    private final ConcurrentMap<UUID, Aggregator<VehicleTelemetry, VehicleStatistic>> ongoingAggregators;

    private final StatisticService statisticService;

    public TelemetryAggregationEngine(final ObjectPool pool,
                                      final StatisticService statisticService) {
        this(pool, new ConcurrentHashMap<>(0), statisticService);
    }

    TelemetryAggregationEngine(final ObjectPool aggregatorPool,
                               final ConcurrentMap<UUID, Aggregator<VehicleTelemetry, VehicleStatistic>> ongoingAggregators,
                               final StatisticService statisticService) {
        Objects.requireNonNull(aggregatorPool, "Aggregator pool must not be null");
        Objects.requireNonNull(ongoingAggregators, "Aggregators map must not be null");
        Objects.requireNonNull(statisticService, "statisticService must not be null");

        this.aggregatorPool = aggregatorPool;
        this.ongoingAggregators = ongoingAggregators;
        this.statisticService = statisticService;
    }

    public void aggregateTelemetry(final UUID id, final VehicleTelemetry telemetry) {
        LOGGER.info("[TELEMETRY-INFO]: telemetryId={}, Dispatched telemetry for aggregation.", id);

        if (!ongoingAggregators.containsKey(id)) {
            tryToObtainAggregator(id);
        }
        ongoingAggregators.get(id).add(telemetry);
    }

    /**
     * Retries until an aggregator has been obtained.
     */
    private void tryToObtainAggregator(final UUID routingKey) {

        while (!registerAggregator(routingKey) && !Thread.currentThread().isInterrupted()) {
            try {
                // In case the aggregator could not be registered, try again.
                Thread.sleep(OFFER_SLEEP_MILLIS);
            } catch (final InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    /**
     * Returns true if aggregator registration was successful. False if no aggregator instance could be retrieved.
     */
    private synchronized boolean registerAggregator(final UUID id) {
        final Aggregator<VehicleTelemetry, VehicleStatistic> aggregator =
                (Aggregator<VehicleTelemetry, VehicleStatistic>) this.aggregatorPool.getInstanceIfFree();

        if (aggregator == null) {
            return false;
        }

        aggregator.lock(id);
        this.ongoingAggregators.putIfAbsent(id, aggregator);

        return true;
    }

    /**
     * Periodically releases accumulated aggregators, persisting their result.
     */
    @Scheduled(fixedDelayString = "${engine.aggregation.refresh-time}")
    public void releaseAggregator() {
        LOGGER.info("[AGGREGATION]: Aggregation accumulation timeframe passed, releasing aggregators.");

        for (final Map.Entry<UUID, Aggregator<VehicleTelemetry, VehicleStatistic>> aggregatorEntry: this.ongoingAggregators.entrySet()) {
            synchronized (aggregatorEntry.getKey()) {
                final Aggregator<VehicleTelemetry, VehicleStatistic> aggregator = aggregatorEntry.getValue();

                LOGGER.info("[AGGREGATION]: Releasing aggregator for id: " + aggregator.getLockId());

                // Persist statistic
                this.statisticService.persistStatistic(aggregator.aggregate());

                // Release aggregator
                aggregator.release();

                this.ongoingAggregators.remove(aggregatorEntry.getKey());
                this.aggregatorPool.freeInstance(aggregator);
            }
        }
    }
}
