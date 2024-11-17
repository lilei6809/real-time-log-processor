package com.lanlan.mock.alert.model;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "alert_rules")
@Data
public class AlertRule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AlertType type;

    @Column(nullable = false)
    private String metric;  // 监控指标 (例如: "404", "5xx")

    @Column(nullable = false)
    private String operator; // >, <, =, >=, <=

    @Column(nullable = false)
    private Double threshold;

    @Column(name = "time_window", nullable = false)
    private String timeWindow; // 1m, 5m, 15m, 1h

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AlertLevel level;

    private Boolean enabled = true;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
