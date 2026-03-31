package com.lpu.auth_service.observability;

import io.micrometer.tracing.Span;
import io.micrometer.tracing.Tracer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.boot.context.event.ApplicationReadyEvent;

@Component
public class StartupTraceEmitter {

    private static final Logger log = LoggerFactory.getLogger(StartupTraceEmitter.class);

    private final Tracer tracer;
    private final Environment environment;

    public StartupTraceEmitter(Tracer tracer, Environment environment) {
        this.tracer = tracer;
        this.environment = environment;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void emitStartupTrace() {
        emitTrace("service.startup");
    }

    @Scheduled(
            initialDelayString = "${finflow.tracing.initial-delay-ms:10000}",
            fixedDelayString = "${finflow.tracing.heartbeat-ms:30000}"
    )
    public void emitHeartbeatTrace() {
        emitTrace("service.heartbeat");
    }

    private void emitTrace(String spanName) {
        String serviceName = environment.getProperty("spring.application.name", "unknown-service");
        Span span = tracer.nextSpan().name(spanName).start();
        try (Tracer.SpanInScope ignored = tracer.withSpan(span)) {
            span.tag("service.name", serviceName);
            log.info("Emitting {} trace for {}", spanName, serviceName);
        }
        finally {
            span.end();
        }
    }
}
