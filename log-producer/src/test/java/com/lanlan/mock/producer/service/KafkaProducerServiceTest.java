package com.lanlan.mock.producer.service;

import com.lanlan.mock.common.error.LogProcessingException;
import com.lanlan.mock.common.model.AccessLog;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.errors.TimeoutException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class KafkaProducerServiceTest {

    @Mock
    private KafkaTemplate<String, AccessLog> kafkaTemplate;

    private KafkaProducerService producerService;

    @BeforeEach
    void setUp() {
        producerService = new KafkaProducerService(kafkaTemplate);
        ReflectionTestUtils.setField(producerService, "topic", "test-topic");
    }

    @Test
    @DisplayName("成功发送日志不应该重试")
    void shouldSendLogSuccessfullyWithoutRetry() {
        // 准备测试数据
        AccessLog accessLog = createSampleAccessLog();

        // Mock Kafka发送成功
        CompletableFuture<SendResult<String, AccessLog>> future =
                CompletableFuture.completedFuture(mock(SendResult.class));
        when(kafkaTemplate.send(any(ProducerRecord.class))).thenReturn(future);

        // 执行测试
        assertDoesNotThrow(() -> producerService.sendLog(accessLog));

        // 验证只调用了一次send
        verify(kafkaTemplate, times(1)).send(any(ProducerRecord.class));
    }

    @Test
    @DisplayName("临时错误应该触发重试并最终成功")
    void shouldRetryOnTemporaryFailureAndSucceed() {
        // 准备测试数据
        AccessLog accessLog = createSampleAccessLog();

        // 创建失败的 Future
        CompletableFuture<SendResult<String, AccessLog>> failedFuture = new CompletableFuture<>();
        failedFuture.completeExceptionally(new TimeoutException("Timeout"));

        // 创建成功的 Future
        CompletableFuture<SendResult<String, AccessLog>> successFuture =
                CompletableFuture.completedFuture(mock(SendResult.class));

        // 第一次返回失败，第二次返回成功
        when(kafkaTemplate.send(any(ProducerRecord.class)))
                .thenReturn(failedFuture)
                .thenReturn(successFuture);

        // 执行测试
        assertDoesNotThrow(() -> producerService.sendLog(accessLog));

        // 验证调用了两次send
        verify(kafkaTemplate, times(2)).send(any(ProducerRecord.class));
    }

    @Test
    @DisplayName("达到最大重试次数后应该抛出异常")
    void shouldThrowExceptionAfterMaxRetries() {
        // 准备测试数据
        AccessLog accessLog = createSampleAccessLog();

        // 创建一个始终失败的 Future
        CompletableFuture<SendResult<String, AccessLog>> failedFuture = new CompletableFuture<>();
        failedFuture.completeExceptionally(new TimeoutException("Timeout"));

        // 所有调用都返回失败
        when(kafkaTemplate.send(any(ProducerRecord.class))).thenReturn(failedFuture);

        // 执行测试并验证异常
        LogProcessingException exception = assertThrows(
                LogProcessingException.class,
                () -> producerService.sendLog(accessLog)
        );

        // 打印异常链，帮助调试
        System.out.println("Exception: " + exception);
        System.out.println("Cause: " + exception.getCause());
        if (exception.getCause() != null) {
            System.out.println("Cause's cause: " + exception.getCause().getCause());
        }

        // 验证异常链
        Throwable rootCause = exception;
        while (rootCause.getCause() != null) {
            rootCause = rootCause.getCause();
        }
        assertTrue(rootCause instanceof TimeoutException,
                "Expected root cause to be TimeoutException, but was " + rootCause.getClass());
        assertEquals("Timeout", rootCause.getMessage());

        // 验证调用了3次send（初始 + 2次重试）
        verify(kafkaTemplate, times(3)).send(any(ProducerRecord.class));
    }

    private AccessLog createSampleAccessLog() {
        AccessLog log = new AccessLog();
        log.setIp("192.168.1.1");
        log.setTimestamp(LocalDateTime.now());
        log.setMethod("GET");
        log.setUrl("/api/test");
        log.setStatusCode(200);
        log.setUserAgent("Mozilla/5.0");
        return log;
    }
}