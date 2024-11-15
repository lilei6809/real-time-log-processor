package com.lanlan.mock.common.util;

import com.lanlan.mock.common.model.AccessLog;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class LogParserTest {

    @Test
    void testParseValidLogLine() {
        String logLine = "54.36.149.41 - - [22/Jan/2019:03:56:14 +0330] \"GET /filter/27|13 HTTP/1.1\" 200 30577 \"-\" \"Mozilla/5.0\" \"-\"";

        AccessLog accessLog = LogParser.parse(logLine);

        assertNotNull(accessLog);
        assertEquals("54.36.149.41", accessLog.getIp());
        assertEquals("GET", accessLog.getMethod());
        assertEquals("/filter/27|13", accessLog.getUrl());
        assertEquals(200, accessLog.getStatusCode());
        assertEquals(30577, accessLog.getResponseSize());
        assertEquals("Mozilla/5.0", accessLog.getUserAgent());
    }

    @Test
    void testParseInvalidLogLine() {
        String invalidLogLine = "Invalid log line format";
        AccessLog accessLog = LogParser.parse(invalidLogLine);
        assertNull(accessLog);
    }
}