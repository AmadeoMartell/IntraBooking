package com.epam.capstone.health;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.Map;

public interface HealthIndicator {
    String getName();

    HealthStatus checkHealth();
}


