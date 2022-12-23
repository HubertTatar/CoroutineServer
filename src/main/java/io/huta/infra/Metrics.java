package io.huta.infra;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.binder.jvm.*;
import io.micrometer.core.instrument.binder.system.FileDescriptorMetrics;
import io.micrometer.core.instrument.binder.system.ProcessorMetrics;
import io.micrometer.core.instrument.binder.system.UptimeMetrics;
import io.micrometer.prometheus.PrometheusConfig;
import io.micrometer.prometheus.PrometheusMeterRegistry;

import java.util.List;

public class Metrics {

    public static PrometheusMeterRegistry createRegistry() {
        var prometheusRegistry = new PrometheusMeterRegistry(PrometheusConfig.DEFAULT);
        List.of(new JvmMemoryMetrics(), new JvmGcMetrics(), new JvmThreadMetrics(), new JvmCompilationMetrics(),
                new ClassLoaderMetrics(), new UptimeMetrics(), new ProcessorMetrics(), new FileDescriptorMetrics())
                .forEach(m -> m.bindTo(prometheusRegistry));

        return prometheusRegistry;
    }
}
