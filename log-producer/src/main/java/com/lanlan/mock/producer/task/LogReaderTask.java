package com.lanlan.mock.producer.task;

import com.lanlan.mock.common.model.AccessLog;
import com.lanlan.mock.common.util.LogParser;
import com.lanlan.mock.producer.reader.LogFileReader;
import com.lanlan.mock.producer.service.KafkaProducerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class LogReaderTask {

    private final LogFileReader logFileReader;
    private final KafkaProducerService kafkaProducerService;

    @Scheduled(fixedDelayString = "${log.reader.interval:1000}")
    public void readAndSendLogs() {
        try {
            String logLine = logFileReader.readLine();
            if (logLine != null) {
                AccessLog accessLog = LogParser.parse(logLine);
                if (accessLog != null) {
                    kafkaProducerService.sendLog(accessLog);
                }
            }
        } catch (Exception e) {
            log.error("Error in log reading task", e);
        }
    }
}