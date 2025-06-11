package com.epam.capstone.health.indicators;

import com.epam.capstone.health.HealthIndicator;
import com.epam.capstone.health.HealthStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.lang.management.ManagementFactory;
import java.lang.management.MemoryUsage;
import java.util.HashMap;
import java.util.Map;

/**
 * Health indicator that monitors JVM heap memory usage.
 * Reports DOWN if usage exceeds a configured threshold.
 */
@Component
@Slf4j
public class JvmMemoryHealthIndicator implements HealthIndicator {

    /**
     * Threshold above which heap usage is considered unhealthy (fraction of max).
     */
    private static final double USAGE_THRESHOLD = 0.8;

    @Override
    public String getName() {
        return "jvmMemory";
    }

    @Override
    public HealthStatus checkHealth() {
        try {
            MemoryUsage heapUsage = ManagementFactory.getMemoryMXBean().getHeapMemoryUsage();
            long used = heapUsage.getUsed();
            long max = heapUsage.getMax();
            double usageRatio = (double) used / max;

            Map<String, Object> details = new HashMap<>();
            details.put("usedBytes", used);
            details.put("maxBytes", max);
            details.put("usageRatio", String.format("%.2f", usageRatio));

            if (usageRatio < USAGE_THRESHOLD) {
                return new HealthStatus(getName(), HealthStatus.Status.UP, details);
            } else {
                log.warn("Heap usage {} above threshold {}", used, USAGE_THRESHOLD);
                details.put("error", "Heap usage above threshold");
                return new HealthStatus(getName(), HealthStatus.Status.DOWN, details);
            }
        } catch (Exception ex) {
            log.error("Exception during JVM memory health check", ex);
            Map<String, Object> details = new HashMap<>();
            details.put("exception", ex.getClass().getSimpleName());
            details.put("message", ex.getMessage());
            return new HealthStatus(getName(), HealthStatus.Status.DOWN, details);
        }
    }
}

