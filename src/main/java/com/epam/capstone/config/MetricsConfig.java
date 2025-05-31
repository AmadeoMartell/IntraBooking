package com.epam.capstone.config;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.binder.jvm.JvmGcMetrics;
import io.micrometer.core.instrument.binder.jvm.JvmMemoryMetrics;
import io.micrometer.core.instrument.binder.jvm.JvmThreadMetrics;
import io.micrometer.core.instrument.binder.system.ProcessorMetrics;
import io.micrometer.core.instrument.binder.system.UptimeMetrics;
import io.micrometer.prometheusmetrics.PrometheusConfig;
import io.micrometer.prometheusmetrics.PrometheusMeterRegistry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.concurrent.TimeUnit;

@Configuration
public class MetricsConfig {

    @Bean
    public PrometheusMeterRegistry prometheusMeterRegistry() {
        return new PrometheusMeterRegistry(PrometheusConfig.DEFAULT);
    }

    @Bean
    public JvmMemoryMetrics jvmMemoryMetrics(MeterRegistry registry) {
        JvmMemoryMetrics metrics = new JvmMemoryMetrics();
        metrics.bindTo(registry);
        return metrics;
    }

    @Bean
    public JvmGcMetrics jvmGcMetrics(MeterRegistry registry) {
        JvmGcMetrics metrics = new JvmGcMetrics();
        metrics.bindTo(registry);
        return metrics;
    }

    @Bean
    public JvmThreadMetrics jvmThreadMetrics(MeterRegistry registry) {
        JvmThreadMetrics metrics = new JvmThreadMetrics();
        metrics.bindTo(registry);
        return metrics;
    }

    @Bean
    public ProcessorMetrics processorMetrics(MeterRegistry registry) {
        ProcessorMetrics metrics = new ProcessorMetrics();
        metrics.bindTo(registry);
        return metrics;
    }

    @Bean
    public UptimeMetrics uptimeMetrics(MeterRegistry registry) {
        UptimeMetrics metrics = new UptimeMetrics();
        metrics.bindTo(registry);
        return metrics;
    }

    @Bean
    public WebMvcConfigurer metricsWebMvcConfigurer(PrometheusMeterRegistry prometheusMeterRegistry) {
        return new WebMvcConfigurer() {
            @Override
            public void addInterceptors(InterceptorRegistry registry) {
                final MeterRegistry meterRegistryRef = prometheusMeterRegistry;

                registry.addInterceptor(new HandlerInterceptor() {
                    @Override
                    public boolean preHandle(HttpServletRequest request,
                                             HttpServletResponse response,
                                             Object handler) {

                        request.setAttribute("startTimeNano", System.nanoTime());
                        return true;
                    }

                    @Override
                    public void afterCompletion(HttpServletRequest request,
                                                HttpServletResponse response,
                                                Object handler,
                                                Exception ex) {

                        Object startAttr = request.getAttribute("startTimeNano");
                        if (startAttr instanceof Long) {
                            long startTime = (Long) startAttr;
                            long duration = System.nanoTime() - startTime;

                            String method = request.getMethod();
                            String uri = request.getRequestURI();
                            int status = response.getStatus();

                            Timer.builder("http.server.requests")
                                    .description("HTTP requests duration")
                                    .tag("method", method)
                                    .tag("uri", uri)
                                    .tag("status", String.valueOf(status))
                                    .publishPercentileHistogram()
                                    .register(meterRegistryRef)
                                    .record(duration, TimeUnit.NANOSECONDS);
                        }
                    }
                }).addPathPatterns("/**");
            }
        };
    }
}
