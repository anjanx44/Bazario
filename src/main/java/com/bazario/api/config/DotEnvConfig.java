package com.bazario.api.config;

import io.github.cdimascio.dotenv.Dotenv;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.ConfigurableEnvironment;

@Configuration
public class DotEnvConfig {

    private final ConfigurableEnvironment environment;

    public DotEnvConfig(ConfigurableEnvironment environment) {
        this.environment = environment;
    }

    @PostConstruct
    public void loadEnvVariables() {
        try {
            Dotenv dotenv = Dotenv.configure()
                    .ignoreIfMalformed()
                    .ignoreIfMissing()
                    .load();

            dotenv.entries().forEach(entry -> {
                String key = entry.getKey();
                String value = entry.getValue();

                // Only set if not already set by system environment
                if (System.getenv(key) == null) {
                    System.setProperty(key, value);
                    System.out.println("Loaded environment variable: " + key);
                }
            });

            System.out.println("Environment variables loaded successfully from .env file");
        } catch (Exception e) {
            System.out.println("Could not load .env file: " + e.getMessage() + ". Using default values.");
        }
    }
}