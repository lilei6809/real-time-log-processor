package com.lanlan.mock.alert.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "alert.mail")
@Data
public class EmailProperties {
    private String from;
    private String[] to;
    private Boolean enabled = true;
}
