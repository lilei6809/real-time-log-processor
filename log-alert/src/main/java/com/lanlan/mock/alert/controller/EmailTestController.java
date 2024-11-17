package com.lanlan.mock.alert.controller;

import com.lanlan.mock.alert.service.MailjetEmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/alert/email")
@Slf4j
@RequiredArgsConstructor
public class EmailTestController {

    private final MailjetEmailService emailService;

    @PostMapping("/test")
    public ResponseEntity<String> testEmail() {
        try {
            emailService.sendTestEmail();
            return ResponseEntity.ok("测试邮件发送成功");
        } catch (Exception e) {
            log.error("测试邮件发送失败", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("测试邮件发送失败: " + e.getMessage());
        }
    }
}
