package com.epam.capstone.health.indicators;

import com.epam.capstone.health.HealthIndicator;
import com.epam.capstone.health.HealthStatus;
import com.epam.capstone.util.database.ConnectionPool;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

/**
 * Health indicator that checks availability of connections in the custom connection pool.
 */
@Component
@Slf4j
public class ConnectionPoolHealthIndicator implements HealthIndicator {

    private final ConnectionPool connectionPool;

    /**
     * Create a new ConnectionPoolHealthIndicator.
     *
     * @param connectionPool the custom connection pool to monitor
     */
    @Autowired
    public ConnectionPoolHealthIndicator(DataSource connectionPool) {
        this.connectionPool = (ConnectionPool) connectionPool;
        log.debug("Initialized ConnectionPoolHealthIndicator");
    }

    @Override
    public String getName() {
        return "connectionPool";
    }

    @Override
    public HealthStatus checkHealth() {
        try {
            int total = connectionPool.getTotalConnections();
            int used = connectionPool.getActiveConnections();
            int free = total - used;

            Map<String, Object> details = new HashMap<>();
            details.put("totalConnections", total);
            details.put("usedConnections", used);
            details.put("freeConnections", free);

            if (free > 0) {
                return new HealthStatus(getName(), HealthStatus.Status.UP, details);
            } else {
                log.warn("No free connections: {}/{} used", used, total);
                details.put("error", "No free connections available");
                return new HealthStatus(getName(), HealthStatus.Status.DOWN, details);
            }
        } catch (Exception ex) {
            log.error("Exception during ConnectionPool health check", ex);
            Map<String, Object> details = new HashMap<>();
            details.put("exception", ex.getClass().getSimpleName());
            details.put("message", ex.getMessage());
            return new HealthStatus(getName(), HealthStatus.Status.DOWN, details);
        }
    }
}

