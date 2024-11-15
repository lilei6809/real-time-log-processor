package com.lanlan.mock.common.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor                 // 添加无参构造函数
@AllArgsConstructor
public class AccessLog {
    private String ip;
    private LocalDateTime timestamp;
    private String method;
    private String url;
    private Integer statusCode;
    private Long responseSize;
    private String referer;
    private String userAgent;
    private String rawLog;  // 保存原始日志内容
}