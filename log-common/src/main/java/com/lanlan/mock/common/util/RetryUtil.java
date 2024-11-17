package com.lanlan.mock.common.util;

import com.lanlan.mock.common.error.ErrorCode;
import com.lanlan.mock.common.error.LogProcessingException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RetryUtil {
    private static final Logger logger = LoggerFactory.getLogger(RetryUtil.class);

    public interface RetryableOperation<T> {
        T execute() throws Exception;
    }

    public static <T> T retry(RetryableOperation<T> operation,
                              int maxAttempts,
                              long delayMs,
                              Class<? extends Exception>... retryableExceptions) {
        int attempts = 0;
        Throwable lastException = null;

        while (true) {
            try {
                return operation.execute();
            } catch (Exception e) {
                // 获取根本原因
                Throwable rootCause = e;
                while (rootCause.getCause() != null) {
                    rootCause = rootCause.getCause();
                }

                // 检查是否是可重试的异常
                boolean isRetryable = false;
                for (Class<? extends Exception> retryableException : retryableExceptions) {
                    if (retryableException.isInstance(rootCause)) {
                        isRetryable = true;
                        break;
                    }
                }

                if (!isRetryable) {
                    if (e instanceof RuntimeException) {
                        throw (RuntimeException) e;
                    }
                    throw new RuntimeException(e);
                }

                attempts++;
                lastException = e;

                if (attempts >= maxAttempts) {
                    throw new LogProcessingException(ErrorCode.DATA_PROCESSING_ERROR,
                            "Operation failed after " + attempts + " attempts", e);
                }

                logger.warn("Retry attempt {} of {} failed: {}",
                        attempts, maxAttempts, rootCause.getMessage());

                try {
                    Thread.sleep(delayMs * attempts);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new LogProcessingException(ErrorCode.DATA_PROCESSING_ERROR,
                            "Retry interrupted", ie);
                }
            }
        }
    }


}