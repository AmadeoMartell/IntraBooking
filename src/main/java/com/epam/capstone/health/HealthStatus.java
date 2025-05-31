package com.epam.capstone.health;

import lombok.Data;

import java.util.Map;

@Data
public class HealthStatus {
    private final String name;
    private final Status status;
    private final Map<String, Object> details;

    public enum Status {UP, DOWN, UNKNOWN}
}
