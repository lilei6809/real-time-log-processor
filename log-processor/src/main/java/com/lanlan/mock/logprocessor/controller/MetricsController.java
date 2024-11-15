package com.lanlan.mock.logprocessor.controller;

import com.lanlan.mock.logprocessor.service.MetricsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/metrics")
@RequiredArgsConstructor
public class MetricsController {

    private final MetricsService metricsService;

    @GetMapping
    public Map<String, Object> getMetrics() {
        return metricsService.getMetrics();
    }
}