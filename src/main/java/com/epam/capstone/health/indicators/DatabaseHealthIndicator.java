package com.epam.capstone.health.indicators;

import com.epam.capstone.health.HealthIndicator;
import com.epam.capstone.health.HealthStatus;
import com.epam.capstone.util.database.CustomJdbcTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Health indicator that validates basic database connectivity by executing a simple query.
 */
@Component
@Slf4j
public class DatabaseHealthIndicator implements HealthIndicator {

    private final CustomJdbcTemplate jdbcTemplate;

    /**
     * Create a new DatabaseHealthIndicator.
     *
     * @param jdbcTemplate the JDBC template for executing queries
     */
    @Autowired
    public DatabaseHealthIndicator(CustomJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        log.debug("Initialized DatabaseHealthIndicator");
    }

    @Override
    public String getName() {
        return "database";
    }

    @Override
    public HealthStatus checkHealth() {
        log.debug("Checking database connectivity");
        try {
            Integer result = jdbcTemplate.queryForObject("SELECT 1", SingleColumnRowMapper.newInstance(Integer.class));
            if (result != null && result == 1) {
                return new HealthStatus(
                        getName(),
                        HealthStatus.Status.UP,
                        Collections.singletonMap("queryResult", result)
                );
            } else {
                log.error("Unexpected result from health query: {}", result);
                Map<String, Object> details = new HashMap<>();
                details.put("queryResult", result);
                details.put("error", "Unexpected result");
                return new HealthStatus(getName(), HealthStatus.Status.DOWN, details);
            }
        } catch (Exception ex) {
            log.error("Exception during database health check", ex);
            Map<String, Object> details = new HashMap<>();
            details.put("exception", ex.getClass().getSimpleName());
            details.put("message", ex.getMessage());
            return new HealthStatus(getName(), HealthStatus.Status.DOWN, details);
        }
    }
}

