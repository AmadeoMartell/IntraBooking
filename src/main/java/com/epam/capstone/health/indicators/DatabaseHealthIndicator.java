package com.epam.capstone.health.indicators;

import com.epam.capstone.health.HealthIndicator;
import com.epam.capstone.health.HealthStatus;
import com.epam.capstone.util.database.CustomJdbcTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Component
public class DatabaseHealthIndicator implements HealthIndicator {

    private final CustomJdbcTemplate jdbcTemplate;

    @Autowired
    public DatabaseHealthIndicator(CustomJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public String getName() {
        return "database";
    }

    @Override
    public HealthStatus checkHealth() {
        try {
            Integer result = jdbcTemplate.queryForObject("SELECT 1", SingleColumnRowMapper.newInstance(Integer.class));
            if (result != null && result == 1) {
                return new HealthStatus(
                        getName(),
                        HealthStatus.Status.UP,
                        Collections.singletonMap("queryResult", result)
                );
            } else {
                Map<String, Object> details = new HashMap<>();
                details.put("queryResult", result);
                details.put("error", "Unexpected result");
                return new HealthStatus(getName(), HealthStatus.Status.DOWN, details);
            }
        } catch (Exception ex) {
            Map<String, Object> details = new HashMap<>();
            details.put("exception", ex.getClass().getSimpleName());
            details.put("message", ex.getMessage());
            return new HealthStatus(getName(), HealthStatus.Status.DOWN, details);
        }
    }
}

