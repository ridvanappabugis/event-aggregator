package com.ridvan.eventaggregator.config;

import com.ridvan.eventaggregator.store.StatisticDataStore;
import com.ridvan.eventaggregator.store.TelemetryDataStore;
import com.ridvan.eventaggregator.store.impl.InMemoryStatisticDataStore;
import com.ridvan.eventaggregator.store.impl.InMemoryTelemetryDataStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for data stores.
 */
@Configuration
public class DataStoreConfig {

    @Bean
    public StatisticDataStore statisticDataStore() {
        // Current impl returns in-memory, replace with db
        return new InMemoryStatisticDataStore();
    }

    @Bean
    public TelemetryDataStore telemetryDataStore() {
        // Current impl returns in-memory, replace with db/fs
        return new InMemoryTelemetryDataStore();
    }
}
