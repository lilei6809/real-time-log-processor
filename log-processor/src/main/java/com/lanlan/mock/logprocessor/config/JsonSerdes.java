package com.lanlan.mock.logprocessor.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.lanlan.mock.common.model.AccessLog;
import org.apache.kafka.common.serialization.Serde;
import org.apache.kafka.common.serialization.Serdes;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;

public class JsonSerdes {
    private static final ObjectMapper mapper = new ObjectMapper()
            .registerModule(new JavaTimeModule());  // 添加 Java 时间模块

    public static Serde<AccessLog> AccessLog() {
        JsonSerializer<AccessLog> serializer = new JsonSerializer<>(mapper);
        JsonDeserializer<AccessLog> deserializer = new JsonDeserializer<>(AccessLog.class, mapper)
                .trustedPackages("*");  // 信任所有包
        return Serdes.serdeFrom(serializer, deserializer);
    }
}