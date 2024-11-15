package com.lanlan.mock.logprocessor.processor;

import com.lanlan.mock.common.model.AccessLog;
import com.lanlan.mock.logprocessor.config.JsonSerdes;
import com.lanlan.mock.logprocessor.service.MetricsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.kstream.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Slf4j
@Component
@RequiredArgsConstructor
public class LogStreamProcessor {

    private final MetricsService metricsService;
    private static final Duration WINDOW_SIZE = Duration.ofMinutes(1);

    @Autowired
    void buildPipeline(StreamsBuilder streamsBuilder) {
        // 使用正确的 Serdes 配置
        KStream<String, AccessLog> logStream = streamsBuilder
                .stream("raw-access-logs", Consumed.with(Serdes.String(), JsonSerdes.AccessLog()));

        // IP 统计
        logStream
                .groupBy((key, value) -> value.getIp(),
                        Grouped.with(Serdes.String(), JsonSerdes.AccessLog()))
                .windowedBy(TimeWindows.of(WINDOW_SIZE))
                .count()
                .toStream()
                .foreach((key, count) -> {
                    String ip = key.key();
                    metricsService.updateIPStats(ip, count);
                });

        // URL 统计
        logStream
                .groupBy((key, value) -> value.getUrl(),
                        Grouped.with(Serdes.String(), JsonSerdes.AccessLog()))
                .windowedBy(TimeWindows.of(WINDOW_SIZE))
                .count()
                .toStream()
                .foreach((key, count) -> {
                    String url = key.key();
                    metricsService.updateURLStats(url, count);
                });

        // 错误统计
        logStream
                .filter((key, value) -> value.getStatusCode() >= 400)
                .groupBy((key, value) -> String.valueOf(value.getStatusCode()),
                        Grouped.with(Serdes.String(), JsonSerdes.AccessLog()))
                .windowedBy(TimeWindows.of(WINDOW_SIZE))
                .count()
                .toStream()
                .foreach((key, count) -> {
                    int statusCode = Integer.parseInt(key.key());
                    metricsService.updateErrorStats(statusCode, count);
                });

        // 带宽统计
        logStream
                .mapValues(AccessLog::getResponseSize)
                .groupBy((key, value) -> "bandwidth",
                        Grouped.with(Serdes.String(), Serdes.Long()))
                .windowedBy(TimeWindows.of(WINDOW_SIZE))
                .reduce(Long::sum)
                .toStream()
                .foreach((key, totalBytes) -> {
                    metricsService.updateBandwidth(totalBytes);
                });
    }
}