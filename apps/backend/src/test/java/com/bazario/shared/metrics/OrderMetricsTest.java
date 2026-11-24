package com.bazario.shared.metrics;

import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.Test;

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
    void recordsCreateLatency() {
        Timer.Sample sample = orderMetrics.startCreateTimer();
        orderMetrics.stopCreateTimer(sample);

        assertThat(registry.get("orders.create").timer().count()).isEqualTo(1L);
    }

    @Test
    void doesNothingWhenNoSampleWasStarted() {
        orderMetrics.stopCreateTimer(null);

        assertThat(registry.find("orders.create").timer()).isNull();
        assertThat(registry.find("orders.placed").counter().count()).isZero();
    }
}
