package com.epam.capstone.api.v1;

import com.epam.capstone.health.HealthIndicator;
import com.epam.capstone.health.HealthStatus;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

/**
 * Provides health-check endpoints for all registered {@link HealthIndicator}s.
 * <p>
 * - GET /api/v1/health
 *   Returns a map of each indicator’s {@code status} and {@code details}, plus an
 *   overall status of UP or DOWN.
 * - GET /api/v1/health/{name}
 *   Returns the status and details for a single named indicator, or 404 if not found.
 * </p>
 */
@Controller
@RequestMapping("/api/v1/health")
@Slf4j
public class HealthController {

    private final List<HealthIndicator> indicators;

    /**
     * Create a new HealthController.
     *
     * @param indicators the list of all health indicators to aggregate
     */
    @Autowired
    public HealthController(List<HealthIndicator> indicators) {
        this.indicators = indicators;
        log.debug("HealthController initialized with {} indicators", indicators.size());
    }

    /**
     * Check the health of all indicators.
     *
     * @param request the servlet request (used to log caller IP)
     * @return a {@link ResponseEntity} containing a map:
     *         <ul>
     *           <li>Each indicator name → {status, details}</li>
     *           <li>"overallStatus" → "UP" if all are UP, otherwise "DOWN"</li>
     *         </ul>
     *         HTTP 200 if all UP; HTTP 503 if any DOWN.
     */
    @GetMapping
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getAllHealth(HttpServletRequest request) {
        String remoteIp = request.getRemoteAddr();
        log.info("Health check requested from IP={}", remoteIp);

        Map<String, Object> healthMap = new HashMap<>();
        boolean anyDown = false;

        for (HealthIndicator indicator : indicators) {
            HealthStatus status = indicator.checkHealth();
            log.debug("Indicator '{}' returned status={} details={}",
                    status.getName(), status.getStatus(), status.getDetails());
            Map<String, Object> single = new HashMap<>();
            single.put("status", status.getStatus());
            single.put("details", status.getDetails());
            healthMap.put(status.getName(), single);

            if (status.getStatus() != HealthStatus.Status.UP) {
                anyDown = true;
            }
        }
        String overall = anyDown ? "DOWN" : "UP";
        healthMap.put("overallStatus", overall);
        log.info("Overall health status = {}", overall);

        HttpStatus code = anyDown ? HttpStatus.SERVICE_UNAVAILABLE : HttpStatus.OK;
        return new ResponseEntity<>(healthMap, code);
    }

    /**
     * Check the health of a single named indicator.
     *
     * @param name the name of the indicator to check
     * @return HTTP 200 + {status, details} if found and UP;
     *         HTTP 503 if found but DOWN;
     *         HTTP 404 if no such indicator.
     */
    @GetMapping("/{name}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getOneHealth(@PathVariable("name") String name) {
        log.debug("Single health check for indicator '{}'", name);
        HealthIndicator indicator = indicators.stream()
                .filter(i -> i.getName().equals(name))
                .findFirst()
                .orElse(null);

        if (indicator == null) {
            log.warn("HealthIndicator '{}' not found", name);
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        HealthStatus status = indicator.checkHealth();
        log.debug("Indicator '{}' status = {}", name, status.getStatus());

        Map<String, Object> body = Map.of(
                "status", status.getStatus(),
                "details", status.getDetails()
        );
        HttpStatus code = (status.getStatus() == HealthStatus.Status.UP)
                ? HttpStatus.OK
                : HttpStatus.SERVICE_UNAVAILABLE;
        return new ResponseEntity<>(body, code);
    }
}
