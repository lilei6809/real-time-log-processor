package com.lanlan.mock.producer.service;

import com.lanlan.mock.common.model.AccessLog;
import com.lanlan.mock.common.util.RetryUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.errors.RetriableException;
import org.apache.kafka.common.errors.TimeoutException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class KafkaProducerService {

    private final KafkaTemplate<String, AccessLog> kafkaTemplate;  // 注意这里的泛型改变了

    @Value("${log.kafka.topic}")
    private String topic;


    public void sendLog(AccessLog accessLog) {
        RetryUtil.retry(() -> {
                    ProducerRecord<String, AccessLog> record =
                            new ProducerRecord<>(topic, accessLog.getIp(), accessLog);

                    return kafkaTemplate.send(record)
                            .get(5, TimeUnit.SECONDS); // 添加超时控制
                },
                3, // 最大重试次数
                1000, // 基础延迟时间（毫秒）
                TimeoutException.class,
                RetriableException.class);
    }
}