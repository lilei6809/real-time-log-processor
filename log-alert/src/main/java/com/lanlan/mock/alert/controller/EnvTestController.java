package com.lanlan.mock.alert.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/env")
@Slf4j
public class EnvTestController {

    @Value("${MAIL_USERNAME:not_set}")
    private String mailUsername;

    @Value("${MAIL_PASSWORD:not_set}")
    private String mailPassword;

    @Value("${ALERT_RECEIVERS:not_set}")
    private String alertReceivers;

    @GetMapping("/check")
    public Map<String, String> checkEnv() {
        log.info("Checking environment variables...");
        return Map.of(
                "MAIL_USERNAME", mailUsername,
                "MAIL_PASSWORD", mailPassword,
                "ALERT_RECEIVERS", alertReceivers
        );
    }
}