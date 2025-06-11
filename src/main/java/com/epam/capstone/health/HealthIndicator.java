package com.epam.capstone.health;

/**
 * Contract for a component that performs a health check and reports its status.
 */
public interface HealthIndicator {
    /**
     * @return a unique name identifying this indicator
     */
    String getName();

    /**
     * Execute the health check logic and return a {@link HealthStatus}.
     *
     * @return the current health status of this indicator
     */
    HealthStatus checkHealth();
}


