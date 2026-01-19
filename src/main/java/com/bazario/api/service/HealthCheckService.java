package com.bazario.api.service;

import com.bazario.api.model.dto.HealthCheckResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class HealthCheckService {

    private final DataSource dataSource;

    @Value("${spring.profiles.active}")
    private String activeProfile;

    @Value("${spring.application.name}")
    private String applicationName;

    public HealthCheckResponse performHealthCheck() {
        HealthCheckResponse.DatabaseStatus dbStatus = checkDatabaseHealth();

        return HealthCheckResponse.builder()
                .status("UP")
                .message("Service is running successfully")
                .service(applicationName)
                .environment(activeProfile)
                .timestamp(LocalDateTime.now())
                .database(dbStatus)
                .build();
    }

    private HealthCheckResponse.DatabaseStatus checkDatabaseHealth() {
        try (Connection connection = dataSource.getConnection()) {
            if (connection.isValid(1000)) {
                return HealthCheckResponse.DatabaseStatus.builder()
                        .status("UP")
                        .message("Database connection is healthy")
                        .build();
            } else {
                return HealthCheckResponse.DatabaseStatus.builder()
                        .status("DOWN")
                        .message("Database connection is invalid")
                        .build();
            }
        } catch (SQLException e) {
            return HealthCheckResponse.DatabaseStatus.builder()
                    .status("DOWN")
                    .message("Database connection failed: " + e.getMessage())
                    .build();
        }
    }
}
