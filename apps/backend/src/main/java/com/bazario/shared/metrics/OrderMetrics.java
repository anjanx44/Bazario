package com.bazario.shared.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.stereotype.Component;

/**
 * Order metrics exposed through Micrometer.
 *
 * Kept as a thin component so the order service only depends on two method calls
 * instead of the Micrometer API itself.
 */
@Component
public class OrderMetrics {

    private static final String ORDERS_PLACED = "orders.placed";
    private static final String ORDERS_CREATE = "orders.create";

    private final MeterRegistry registry;
    private final Counter placedCounter;

    public OrderMetrics(MeterRegistry registry) {
        this.registry = registry;
        this.placedCounter = Counter.builder(ORDERS_PLACED)
                .description("Number of orders successfully placed")
                .register(registry);
    }

    /**
     * Starts a latency sample for a single order creation.
     *
     * @return sample that must be passed to {@link #stopCreateTimer(Timer.Sample)} in a finally block
     */
    public Timer.Sample startCreateTimer() {
        return Timer.start(registry);
    }

    /**
     * Stops the latency sample and records it in the orders.create timer.
     * Safe to call when the order creation failed: latency of failures is worth seeing too.
     */
    public void stopCreateTimer(Timer.Sample sample) {
        if (sample == null) {
            return;
        }
        sample.stop(Timer.builder(ORDERS_CREATE)
                .description("Latency of order creation")
                .publishPercentileHistogram()
                .register(registry));
    }

    /**
     * Counts one successfully placed order. Called on the success path only,
     * so the counter never counts failed attempts.
     */
    public void incrementPlaced() {
        placedCounter.increment();
    }
}
