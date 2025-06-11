package com.epam.capstone.api.v1;

import io.micrometer.prometheusmetrics.PrometheusMeterRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Exposes Prometheus-formatted metrics over HTTP for scraping by monitoring tools.
 * <p>
 * The endpoint is mounted at "/metrics" and will return all registered metrics in
 * plain text format.
 * </p>
 */
@Controller
@Slf4j
public class MetricsController {
    private final PrometheusMeterRegistry prometheusRegistry;

    /**
     * Create a new MetricsController.
     *
     * @param prometheusRegistry the registry that holds all Prometheus metrics
     */
    @Autowired
    public MetricsController(PrometheusMeterRegistry prometheusRegistry) {
        this.prometheusRegistry = prometheusRegistry;
        log.debug("MetricsController initialized with registry: {}", prometheusRegistry);
    }

    /**
     * Handle GET requests for "/metrics".
     * <p>
     * Returns all metrics in Prometheus text format, suitable for scraping.
     * </p>
     *
     * @return the concatenated metrics data
     */
    @GetMapping(value = "/metrics", produces = MediaType.TEXT_PLAIN_VALUE)
    @ResponseBody
    public String scrape() {
        log.debug("Scraping metrics...");
        String scraped = prometheusRegistry.scrape();
        log.trace("Scraped metrics payload length: {}", scraped.length());
        return scraped;
    }

}

