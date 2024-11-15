package com.lanlan.mock.common.util;

import com.lanlan.mock.common.model.AccessLog;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class LogParser {
    // 匹配日志格式的正则表达式
    private static final Pattern LOG_PATTERN = Pattern.compile(
            "^(\\S+) \\S+ \\S+ \\[(\\d{2}/\\w{3}/\\d{4}:\\d{2}:\\d{2}:\\d{2} [+-]\\d{4})\\] \"(\\w+) ([^\"]*) HTTP/[\\d.]+\" (\\d{3}) (\\d+) \"([^\"]*)\" \"([^\"]*)\" \"([^\"]*)\"");

    // 日期格式化
    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("dd/MMM/yyyy:HH:mm:ss Z", Locale.ENGLISH);

    public static AccessLog parse(String logLine) {
        try {

            Matcher matcher = LOG_PATTERN.matcher(logLine);
            if (!matcher.find()) {
                log.warn("Unable to parse log line: {}", logLine);
                return null;
            }

            // 解析时间戳
            String timestampStr = matcher.group(2);
            LocalDateTime timestamp = ZonedDateTime
                    .parse(timestampStr, DATE_FORMATTER)
                    .toLocalDateTime();

            // 构建AccessLog对象
            return AccessLog.builder()
                    .ip(matcher.group(1))
                    .timestamp(timestamp)
                    .method(matcher.group(3))
                    .url(matcher.group(4))
                    .statusCode(Integer.parseInt(matcher.group(5)))
                    .responseSize(Long.parseLong(matcher.group(6)))
                    .referer(matcher.group(7))
                    .userAgent(matcher.group(8))
                    .rawLog(logLine)
                    .build();

        } catch (Exception e) {
            log.error("Error parsing log line: {}", logLine, e);
            return null;
        }
    }
}