package com.lanlan.mock.common.error;

public enum ErrorCode {
    // Kafka 相关错误
    KAFKA_PRODUCER_ERROR(1001, "Kafka生产者错误"),
    KAFKA_CONSUMER_ERROR(1002, "Kafka消费者错误"),
    KAFKA_SERIALIZATION_ERROR(1003, "序列化/反序列化错误"),

    // Redis 相关错误
    REDIS_CONNECTION_ERROR(2001, "Redis连接错误"),
    REDIS_OPERATION_ERROR(2002, "Redis操作错误"),

    // 业务处理错误
    INVALID_LOG_FORMAT(3001, "无效的日志格式"),
    DATA_PROCESSING_ERROR(3002, "数据处理错误");

    private final int code;
    private final String description;

    ErrorCode(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }
}

