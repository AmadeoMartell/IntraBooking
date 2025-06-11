package com.epam.capstone.health;

import lombok.Data;

import java.util.Map;


/**
 * Represents the result of a health check performed by a {@link HealthIndicator}.
 */
@Data
public class HealthStatus {
    private final String name;
    private final Status status;
    private final Map<String, Object> details;

    /**
     * Enumeration of possible health states.
     */
    public enum Status {UP, DOWN, UNKNOWN}
}
