package com.bazario.api.model.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class HealthCheckResponse {
    private String status;
    private String message;
    private String service;
    private String environment;
    private LocalDateTime timestamp;
    private DatabaseStatus database;

    @Data
    @Builder
    public static class DatabaseStatus {
        private String status;
        private String message;
    }
}
