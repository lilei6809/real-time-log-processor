package com.lanlan.mock.alert.controller;

import com.lanlan.mock.alert.config.EmailProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RefreshScope  // 支持配置热刷新
@RequestMapping("/api/config")
@Slf4j
public class ConfigTestController {

    @Value("${spring.mail.host:}")
    private String mailHost;

    @Value("${alert.mail.enabled:false}")
    private Boolean mailEnabled;

    @Autowired
    private EmailProperties emailProperties;

    @GetMapping("/mail")
    public Map<String, Object> getMailConfig() {
        return Map.of(
                "host", mailHost,
                "enabled", mailEnabled,
                "from", emailProperties.getFrom(),
                "to", emailProperties.getTo()
        );
    }
}
