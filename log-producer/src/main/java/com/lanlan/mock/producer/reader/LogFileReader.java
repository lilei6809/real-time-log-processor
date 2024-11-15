package com.lanlan.mock.producer.reader;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

@Slf4j
@Component
public class LogFileReader {

    @Value("${log.file.path}")
    private String logFilePath;

    private BufferedReader reader;

    public String readLine() {
        try {
            if (reader == null) {
                reader = new BufferedReader(new FileReader(logFilePath));
                log.info("Opened log file: {}", logFilePath);
            }

            String line = reader.readLine();
            if (line == null) {
                // 文件读完了，重新从头开始
                reader.close();
                reader = new BufferedReader(new FileReader(logFilePath));
                line = reader.readLine();
            }
            return line;

        } catch (IOException e) {
            log.error("Error reading log file", e);
            return null;
        }
    }
}