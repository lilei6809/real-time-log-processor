# Real-time Log Processing System

A distributed system for real-time log collection, processing, analysis and alerting using Kafka Streams, Redis and Spring Cloud Config.

## Architecture Overview

```
real-time-log-processor/
├── log-common/          # Shared models and utilities
├── log-producer/        # Log producer service
├── log-processor/       # Real-time stream processing service
├── log-alert/          # Alert service
└── log-config/         # Centralized configuration
```

## Key Features

- Real-time log collection and processing
- Multi-dimensional statistics (IP, URL, error rates, bandwidth)
- Fault-tolerant message processing with retry mechanism
- Redis-based metrics storage
- Intelligent alerting system with multiple levels
- Email notifications via Mailjet
- Centralized configuration management
- JSON serialization/deserialization support
- Configurable processing windows
- Extensible statistics dimensions

## Tech Stack

- **Framework:** Spring Boot, Spring Cloud
- **Message Queue:** Apache Kafka
- **Stream Processing:** Kafka Streams
- **Storage:** Redis
- **Config Server:** Spring Cloud Config
- **Email Service:** Mailjet
- **Build Tool:** Maven

## Prerequisites

- JDK 11+
- Maven 3.6+
- Kafka 2.8+
- Redis 6+
- Spring Cloud Config Server

## Configuration

### Config Server Setup

```yaml
spring:
  cloud:
    config:
      server:
        git:
          uri: ${CONFIG_REPO_URI}
          username: ${CONFIG_REPO_USERNAME}
          password: ${CONFIG_REPO_PASSWORD}
```

### Alert Configuration

```yaml
alert:
  email:
    api-key: ${MAILJET_API_KEY}
    api-secret: ${MAILJET_API_SECRET}
    from-email: ${ALERT_FROM_EMAIL}
    from-name: ${ALERT_FROM_NAME}
```

[Previous Kafka and Redis configurations remain the same...]

## Project Structure

- **log-common**
  - Shared models (AccessLog)
  - Common utilities
  - Error handling

- **log-producer**
  - Log file monitoring
  - Kafka message production
  - Retry mechanism

- **log-processor**
  - Kafka Streams processing
  - Redis metrics storage
  - Real-time statistics

- **log-alert**
  - Alert rules management
  - Email notification service
  - Alert history tracking
  - Multiple alert levels (INFO, WARN, ERROR)

- **log-config**
  - Centralized configuration
  - Environment-specific properties
  - Sensitive data management

## Alert System

### Alert Levels
- INFO: General information
- WARN: Warning conditions
- ERROR: Critical issues

### Alert Types
- Performance alerts
- Error rate alerts
- Security alerts
- System health alerts

### Alert Rules
Rules can be configured for:
- Error rate thresholds
- Response time thresholds
- Traffic anomalies
- Security violations

## Development Status

- [x] Basic framework setup
- [x] Real-time statistics implementation
- [x] Redis storage integration
- [x] Serialization handling
- [x] Alert system implementation
- [x] Email notification service
- [x] Centralized configuration
- [ ] Geographic location analysis
- [ ] User agent analysis
- [ ] Access path analysis
- [ ] Performance optimization
- [ ] Enhanced monitoring

```
