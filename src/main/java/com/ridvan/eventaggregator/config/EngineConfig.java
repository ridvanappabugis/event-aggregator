package com.ridvan.eventaggregator.config;

import com.ridvan.eventaggregator.aggregation.engine.TelemetryAggregationEngine;
import com.ridvan.eventaggregator.aggregation.engine.Aggregator;
import com.ridvan.eventaggregator.aggregation.engine.TelemetryAggregator;
import com.ridvan.eventaggregator.services.StatisticService;
import com.sun.org.apache.xml.internal.utils.ObjectPool;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for the aggregation engine.
 */
@Configuration
public class EngineConfig {

    @Bean
    public Integer maxAggregators(@Value("${engine.aggregation.max}") final Integer maxAggregators) {
        return maxAggregators != null ? maxAggregators : 10;
    }

    /**
     * Object Pool for Aggregators.
     * <p>
     * Reason: To avoid instantiation/gc overhead, considering that they are short-lived.
     */
    @Bean
    public ObjectPool aggregatorObjectPool(@Qualifier("maxAggregators") final Integer maxAggregators) {
        final ObjectPool objectPool = new ObjectPool(Aggregator.class, maxAggregators);

        for (int i = 0; i < maxAggregators; i++) {
            objectPool.freeInstance(new TelemetryAggregator());
        }

        return objectPool;
    }

    @Bean
    public TelemetryAggregationEngine aggregationEngine(@Qualifier("aggregatorObjectPool") final ObjectPool aggregatorObjectPool,
                                                        final StatisticService statisticDataStore) {
        return new TelemetryAggregationEngine(aggregatorObjectPool, statisticDataStore);
    }
}
