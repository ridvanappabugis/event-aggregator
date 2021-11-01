package com.ridvan.eventaggregator.event.router;

import java.util.UUID;

/**
 * An event router. Routes provided events to their respective destinations.
 */
public interface EventRouter {

    /**
     * Routes the provided event to its correct destination, usually synchronously, using the provided routing key.
     *
     * @param routingKey the key to use to determine the correct receiver.
     * @param event the event to route.
     */
    void route(final UUID routingKey, final Object event);
}
