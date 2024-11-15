package com.lanlan.mock.producer.service;

import com.lanlan.mock.common.model.AccessLog;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaProducerService {

    private final KafkaTemplate<String, AccessLog> kafkaTemplate;  // 注意这里的泛型改变了

    @Value("${log.kafka.topic}")
    private String topic;

    public void sendLog(AccessLog accessLog) {
        try {
            kafkaTemplate.send(topic, accessLog.getIp(), accessLog)  // 直接发送 AccessLog 对象
                    .whenComplete((result, ex) -> {
                        if (ex == null) {
                            log.debug("Successfully sent log message: {}", accessLog.getIp());
                        } else {
                            log.error("Failed to send log message for IP: {}", accessLog.getIp(), ex);
                        }
                    });
        } catch (Exception e) {
            log.error("Error sending log message for IP: {}", accessLog.getIp(), e);
        }
    }
}