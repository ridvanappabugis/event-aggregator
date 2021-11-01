package com.ridvan.eventaggregator.aggregation.engine;

import java.util.UUID;

public interface Aggregator<I, R> {

    UUID getLockId();

    void lock(final UUID id);

    void release();

    void add(final I element);

    R aggregate();
}
