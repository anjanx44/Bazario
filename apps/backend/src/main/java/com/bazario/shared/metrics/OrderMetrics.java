package com.bazario.shared.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

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

    static final String OUTCOME_TAG = "outcome";
    static final String OUTCOME_SUCCESS = "success";
    static final String OUTCOME_FAILURE = "failure";

    private final MeterRegistry registry;
    private final Counter placedCounter;

    // Timers are created once and reused: building a Timer on every request would
    // allocate on the hot path for no benefit, the registry deduplicates the meter anyway.
    private final Timer successTimer;
    private final Timer failureTimer;

    public OrderMetrics(MeterRegistry registry) {
        this.registry = registry;
        this.placedCounter = Counter.builder(ORDERS_PLACED)
                .description("Number of orders successfully placed")
                .register(registry);
        this.successTimer = createTimer(registry, OUTCOME_SUCCESS);
        this.failureTimer = createTimer(registry, OUTCOME_FAILURE);
    }

    private static Timer createTimer(MeterRegistry registry, String outcome) {
        return Timer.builder(ORDERS_CREATE)
                .description("Latency of order creation")
                .tag(OUTCOME_TAG, outcome)
                .publishPercentileHistogram()
                .register(registry);
    }

    /**
     * Starts a latency sample for a single order creation.
     *
     * @return sample that must be passed to {@link #stopCreateTimer(Timer.Sample, boolean)} in a finally block
     */
    public Timer.Sample startCreateTimer() {
        return Timer.start(registry);
    }

    /**
     * Stops the latency sample and records it on the timer of the matching outcome.
     * Safe to call when the order creation failed: latency of failures is worth seeing too.
     *
     * @param success {@code true} on the success path, {@code false} when the use case threw
     */
    public void stopCreateTimer(Timer.Sample sample, boolean success) {
        if (sample == null) {
            return;
        }
        sample.stop(success ? successTimer : failureTimer);
    }

    /**
     * Counts one successfully placed order, after the surrounding transaction commits.
     *
     * <p>A commit-time rollback would otherwise leave the counter incremented for an order
     * that never existed, which would make {@code orders.placed} count attempts instead of
     * successes. When no transaction is active (unit tests, non-transactional callers) the
     * increment happens immediately.
     */
    public void incrementPlaced() {
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    placedCounter.increment();
                }
            });
        } else {
            placedCounter.increment();
        }
    }
}
