package com.epam.capstone.api.v1;

import io.micrometer.prometheusmetrics.PrometheusMeterRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class MetricsController {
    private final PrometheusMeterRegistry prometheusRegistry;
    @Autowired
    public MetricsController(PrometheusMeterRegistry prometheusRegistry) {
        this.prometheusRegistry = prometheusRegistry;
    }
    @GetMapping("/metrics")
    @ResponseBody
    public String scrape() {
        return prometheusRegistry.scrape();
    }
}

