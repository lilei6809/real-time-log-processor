package com.lanlan.mock.alert.model;

public enum AlertType {
    ERROR_COUNT("错误数量告警"),
    BANDWIDTH_THRESHOLD("带宽阈值告警"),
    IP_ACCESS_FREQUENCY("IP访问频率告警"),
    URL_ACCESS_FREQUENCY("URL访问频率告警");

    private final String description;

    AlertType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
