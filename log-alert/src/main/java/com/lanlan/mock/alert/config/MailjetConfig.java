package com.lanlan.mock.alert.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "mailjet")
@Data
@EnableConfigurationProperties
public class MailjetConfig {
    private String apiKeyPublic;
    private String apiKeyPrivate;
    private String senderEmail;
    private String senderName = "系统告警";
    private String[] recipients = new String[0];
}
