package com.lanlan.mock.logprocessor.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class MetricsService {
    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;

    private static final String KEY_PREFIX = "log:metrics:";
    private static final String IP_STATS = KEY_PREFIX + "ip";
    private static final String URL_STATS = KEY_PREFIX + "url";
    private static final String ERROR_STATS = KEY_PREFIX + "error";
    private static final String BANDWIDTH = KEY_PREFIX + "bandwidth";
    private static final String LAST_UPDATE = KEY_PREFIX + "lastUpdate";

    // 设置过期时间为5分钟
    private static final long EXPIRE_MINUTES = 5;

    public void updateIPStats(String ip, long count) {
        try {
            log.info("Updating IP stats: {} -> {}", ip, count);  // 添加日志
            redisTemplate.opsForHash().put(IP_STATS, ip, String.valueOf(count));
            redisTemplate.expire(IP_STATS, EXPIRE_MINUTES, TimeUnit.MINUTES);
            updateLastUpdateTime();
        } catch (Exception e) {
            log.error("Error updating IP stats in Redis", e);
        }
    }

    public void updateURLStats(String url, long count) {
        try {
            redisTemplate.opsForHash().put(URL_STATS, url, String.valueOf(count));
            redisTemplate.expire(URL_STATS, EXPIRE_MINUTES, TimeUnit.MINUTES);
            updateLastUpdateTime();
        } catch (Exception e) {
            log.error("Error updating URL stats in Redis", e);
        }
    }

    public void updateErrorStats(int statusCode, long count) {
        try {
            redisTemplate.opsForHash().put(ERROR_STATS,
                    String.valueOf(statusCode), String.valueOf(count));
            redisTemplate.expire(ERROR_STATS, EXPIRE_MINUTES, TimeUnit.MINUTES);
            updateLastUpdateTime();
        } catch (Exception e) {
            log.error("Error updating error stats in Redis", e);
        }
    }

    public void updateBandwidth(long bytes) {
        try {
            redisTemplate.opsForValue().set(BANDWIDTH, String.valueOf(bytes));
            redisTemplate.expire(BANDWIDTH, EXPIRE_MINUTES, TimeUnit.MINUTES);
            updateLastUpdateTime();
        } catch (Exception e) {
            log.error("Error updating bandwidth in Redis", e);
        }
    }

    private void updateLastUpdateTime() {
        try {
            String now = LocalDateTime.now().toString();
            redisTemplate.opsForValue().set(LAST_UPDATE, now);
            redisTemplate.expire(LAST_UPDATE, EXPIRE_MINUTES, TimeUnit.MINUTES);
        } catch (Exception e) {
            log.error("Error updating last update time in Redis", e);
        }
    }

    public Map<String, Object> getMetrics() {
        Map<String, Object> metrics = new HashMap<>();
        try {
            metrics.put("lastUpdateTime", redisTemplate.opsForValue().get(LAST_UPDATE));
            metrics.put("ipAccessCount", redisTemplate.opsForHash().entries(IP_STATS));
            metrics.put("urlAccessCount", redisTemplate.opsForHash().entries(URL_STATS));
            metrics.put("errorStatusCount", redisTemplate.opsForHash().entries(ERROR_STATS));
            metrics.put("totalBandwidth", redisTemplate.opsForValue().get(BANDWIDTH));
        } catch (Exception e) {
            log.error("Error getting metrics from Redis", e);
        }
        return metrics;
    }
}