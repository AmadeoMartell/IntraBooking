package com.epam.capstone.api.v1;

import com.epam.capstone.health.HealthIndicator;
import com.epam.capstone.health.HealthStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

@Controller
@RequestMapping("/api/v1/health")
public class HealthController {

    private final List<HealthIndicator> indicators;

    @Autowired
    public HealthController(List<HealthIndicator> indicators) {
        this.indicators = indicators;
    }

    @GetMapping
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getAllHealth() {
        Map<String, Object> healthMap = new HashMap<>();
        boolean anyDown = false;

        for (HealthIndicator indicator : indicators) {
            HealthStatus status = indicator.checkHealth();
            Map<String, Object> single = new HashMap<>();
            single.put("status", status.getStatus());
            single.put("details", status.getDetails());
            healthMap.put(status.getName(), single);

            if (status.getStatus() != HealthStatus.Status.UP) {
                anyDown = true;
            }
        }

        healthMap.put("overallStatus", anyDown ? "DOWN" : "UP");
        return new ResponseEntity<>(
                healthMap,
                anyDown ? HttpStatus.SERVICE_UNAVAILABLE : HttpStatus.OK
        );
    }

    @GetMapping("/{name}")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getOneHealth(@PathVariable("name") String name) {
        HealthIndicator indicator = indicators.stream()
                .filter(i -> i.getName().equals(name))
                .findFirst()
                .orElse(null);

        if (indicator == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        HealthStatus status = indicator.checkHealth();
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
