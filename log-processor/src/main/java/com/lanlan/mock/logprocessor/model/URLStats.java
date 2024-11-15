package com.lanlan.mock.logprocessor.model;

import lombok.Data;

@Data
public class URLStats {
    private long totalResponseSize = 0;
    private long requestCount = 0;

    public void addResponseSize(long size) {
        totalResponseSize += size;
        requestCount++;
    }

    public double getAverageResponseSize() {
        return requestCount == 0 ? 0 : (double) totalResponseSize / requestCount;
    }
}