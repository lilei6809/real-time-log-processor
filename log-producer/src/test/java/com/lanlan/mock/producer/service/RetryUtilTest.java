package com.lanlan.mock.producer.service;

import com.lanlan.mock.common.error.LogProcessingException;
import com.lanlan.mock.common.util.RetryUtil;
import org.apache.kafka.common.errors.TimeoutException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

class RetryUtilTest {

    @Test
    @DisplayName("成功操作应该立即返回结果")
    void successfulOperationShouldReturnImmediately() {
        String result = RetryUtil.retry(
                () -> "success",
                3,
                100
        );

        assertEquals("success", result);
    }

    @Test
    @DisplayName("可重试异常应该进行重试直到成功")
    void shouldRetryOnRetriableException() {
        AtomicInteger attempts = new AtomicInteger(0);

        String result = RetryUtil.retry(
                () -> {
                    if (attempts.incrementAndGet() < 3) {
                        throw new RuntimeException("Temporary failure");
                    }
                    return "success after retry";
                },
                3,
                100,
                RuntimeException.class
        );

        assertEquals(3, attempts.get());
        assertEquals("success after retry", result);
    }

    @Test
    @DisplayName("达到最大重试次数后应该抛出异常")
    void shouldThrowExceptionAfterMaxAttempts() {
        assertThrows(LogProcessingException.class, () -> {
            RetryUtil.retry(
                    () -> {
                        throw new RuntimeException("Persistent failure");
                    },
                    3,
                    100,
                    RuntimeException.class
            );
        });
    }

    @Test
    @DisplayName("不可重试的异常应该立即抛出")
    void shouldThrowNonRetriableExceptionImmediately() {
        AtomicInteger attempts = new AtomicInteger(0);

        // 验证非重试异常会立即抛出
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            RetryUtil.retry(
                    () -> {
                        attempts.incrementAndGet();
                        throw new IllegalArgumentException("Invalid argument");
                    },
                    3,
                    100L,
                    TimeoutException.class  // 只有 TimeoutException 是可重试的
            );
        });

        assertEquals("Invalid argument", exception.getMessage());
        assertEquals(1, attempts.get(), "Should only attempt once for non-retriable exception");
    }

    @Test
    @DisplayName("可重试异常应该进行重试直到达到最大次数")
    void shouldRetryUntilMaxAttemptsForRetriableException() {
        AtomicInteger attempts = new AtomicInteger(0);

        assertThrows(LogProcessingException.class, () -> {
            RetryUtil.retry(
                    () -> {
                        attempts.incrementAndGet();
                        throw new TimeoutException("Timeout");
                    },
                    3,
                    100L,
                    TimeoutException.class
            );
        });

        assertEquals(3, attempts.get(), "Should attempt maximum number of times");
    }
}

