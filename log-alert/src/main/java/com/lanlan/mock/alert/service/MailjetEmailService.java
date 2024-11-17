package com.lanlan.mock.alert.service;

import com.lanlan.mock.alert.config.MailjetConfig;
import com.lanlan.mock.alert.model.AlertLevel;
import com.mailjet.client.ClientOptions;
import com.mailjet.client.MailjetClient;
import com.mailjet.client.MailjetRequest;
import com.mailjet.client.MailjetResponse;
import com.mailjet.client.resource.Emailv31;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class MailjetEmailService {

    private final MailjetConfig mailjetConfig;

    private static final Map<AlertLevel, String> LEVEL_COLORS = Map.of(
            AlertLevel.INFO, "#3498db",
            AlertLevel.WARNING, "#f1c40f",
            AlertLevel.ERROR, "#e74c3c",
            AlertLevel.CRITICAL, "#c0392b"
    );

    public void sendAlert(String subject, String content, AlertLevel level) {
        try {
            // 验证配置
            if (mailjetConfig.getApiKeyPublic() == null || mailjetConfig.getApiKeyPrivate() == null) {
                throw new IllegalStateException("Mailjet API keys not configured");
            }
            if (mailjetConfig.getSenderEmail() == null) {
                throw new IllegalStateException("Sender email not configured");
            }
            if (mailjetConfig.getRecipients() == null || mailjetConfig.getRecipients().length == 0) {
                throw new IllegalStateException("No recipients configured");
            }

            // 修改这里：直接创建 MailjetClient，不使用 ClientOptions
            MailjetClient client = new MailjetClient(
                    mailjetConfig.getApiKeyPublic(),
                    mailjetConfig.getApiKeyPrivate()
            );

            // 创建收件人列表
            JSONArray recipients = new JSONArray();
            for (String recipient : mailjetConfig.getRecipients()) {
                recipients.put(new JSONObject()
                        .put("Email", recipient)
                        .put("Name", recipient.split("@")[0]));
            }

            // 创建HTML内容
            String htmlContent = createHtmlContent(content, level);

            MailjetRequest request = new MailjetRequest(Emailv31.resource)
                    .property(Emailv31.MESSAGES, new JSONArray()
                            .put(new JSONObject()
                                    .put(Emailv31.Message.FROM, new JSONObject()
                                            .put("Email", mailjetConfig.getSenderEmail())
                                            .put("Name", mailjetConfig.getSenderName()))
                                    .put(Emailv31.Message.TO, recipients)
                                    .put(Emailv31.Message.SUBJECT, String.format("[%s] %s", level, subject))
                                    .put(Emailv31.Message.HTMLPART, htmlContent)));

            MailjetResponse response = client.post(request);

            if (response.getStatus() == 200) {
                log.info("Alert email sent successfully. Status: {}", response.getStatus());
            } else {
                log.error("Failed to send alert email. Status: {}, Data: {}",
                        response.getStatus(), response.getData());
            }

        } catch (Exception e) {
            log.error("Error sending alert email", e);
            throw new RuntimeException("Failed to send alert email", e);
        }
    }

    private String createHtmlContent(String content, AlertLevel level) {
        // 为不同级别定制不同的样式
        Map<AlertLevel, String> levelStyles = Map.of(
                AlertLevel.INFO, "background-color: #3498db; color: white;",
                AlertLevel.WARNING, "background-color: #f1c40f; color: black;",
                AlertLevel.ERROR, "background-color: #e74c3c; color: white;",
                AlertLevel.CRITICAL, "background-color: #c0392b; color: white;"
        );

        String style = levelStyles.getOrDefault(level, "background-color: #3498db; color: white;");

        return String.format("""
        <div style="font-family: Arial, sans-serif;">
            <div style="%s padding: 10px; border-radius: 5px;">
                <h2>系统告警 - %s</h2>
            </div>
            <div style="margin-top: 20px;">
                <pre style="background-color: #f8f9fa; padding: 15px; border-radius: 5px;">%s</pre>
            </div>
            <div style="margin-top: 20px; color: #666; font-size: 12px;">
                <p>发送时间：%s</p>
                <p>告警级别：%s</p>
            </div>
        </div>
        """, style, level, content, LocalDateTime.now(), level);
    }



    // 测试方法
    public void sendTestEmail() {
        sendAlert(
                "测试告警邮件",
                String.format("""
                这是一封测试告警邮件，用于验证 Mailjet 邮件服务配置。
                
                发送时间：%s
                """, LocalDateTime.now()),
                AlertLevel.INFO
        );
    }
}
