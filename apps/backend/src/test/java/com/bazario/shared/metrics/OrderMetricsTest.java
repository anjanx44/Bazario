package com.bazario.shared.metrics;

import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.Test;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import static org.assertj.core.api.Assertions.assertThat;

class OrderMetricsTest {

    private final SimpleMeterRegistry registry = new SimpleMeterRegistry();
    private final OrderMetrics orderMetrics = new OrderMetrics(registry);

    @Test
    void countsEveryPlacedOrder() {
        orderMetrics.incrementPlaced();
        orderMetrics.incrementPlaced();

        assertThat(registry.get("orders.placed").counter().count()).isEqualTo(2.0);
    }

    @Test
    void recordsSuccessLatencyOnTheSuccessTimer() {
        Timer.Sample sample = orderMetrics.startCreateTimer();
        orderMetrics.stopCreateTimer(sample, true);

        assertThat(registry.get("orders.create").tag("outcome", "success").timer().count()).isEqualTo(1L);
        assertThat(registry.get("orders.create").tag("outcome", "failure").timer().count()).isZero();
    }

    @Test
    void recordsFailureLatencyOnTheFailureTimer() {
        Timer.Sample sample = orderMetrics.startCreateTimer();
        orderMetrics.stopCreateTimer(sample, false);

        assertThat(registry.get("orders.create").tag("outcome", "failure").timer().count()).isEqualTo(1L);
        assertThat(registry.get("orders.create").tag("outcome", "success").timer().count()).isZero();
    }

    @Test
    void doesNothingWhenNoSampleWasStarted() {
        orderMetrics.stopCreateTimer(null, true);

        assertThat(registry.get("orders.create").tag("outcome", "success").timer().count()).isZero();
        assertThat(registry.get("orders.create").tag("outcome", "failure").timer().count()).isZero();
        assertThat(registry.find("orders.placed").counter().count()).isZero();
    }

    @Test
    void defersTheCountUntilAfterCommit() {
        TransactionSynchronizationManager.initSynchronization();
        try {
            orderMetrics.incrementPlaced();

            // Before commit nothing is counted: a rollback would leave a phantom success.
            assertThat(registry.get("orders.placed").counter().count()).isZero();

            TransactionSynchronizationManager.getSynchronizations()
                    .forEach(TransactionSynchronization::afterCommit);

            assertThat(registry.get("orders.placed").counter().count()).isEqualTo(1.0);
        } finally {
            TransactionSynchronizationManager.clearSynchronization();
        }
    }
}
