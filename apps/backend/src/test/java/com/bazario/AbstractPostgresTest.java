package com.bazario;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.postgresql.PostgreSQLContainer;

/**
 * Base class for tests that need a real PostgreSQL.
 *
 * Why a base class instead of @Testcontainers on every test:
 * starting a container costs ~3-5s, so we start ONE container for the whole
 * JVM and share it across all subclasses (official "singleton container" pattern).
 *
 * Subclasses only need:
 *   @DataJpaTest
 *   @AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
 */
public abstract class AbstractPostgresTest {

    // Same image as docker-compose.yaml, so tests and local dev agree on Postgres 17.
    // Testcontainers 2.x dropped the generic parameter, so no <?> here.
    private static final PostgreSQLContainer POSTGRES =
            new PostgreSQLContainer("postgres:17-alpine")
                    .withDatabaseName("bazario_test")
                    .withUsername("test")
                    .withPassword("test");

    // static block: runs once when the class is loaded, BEFORE Spring creates any bean.
    // Docker must be running here; otherwise the test fails fast with a clear error.
    static {
        POSTGRES.start();
    }

    /**
     * Copies the container's random JDBC url/user/password into the Spring Environment.
     * Needed because the port is chosen by Docker at runtime - we cannot hardcode it.
     */
    @DynamicPropertySource
    static void postgresProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRES::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRES::getUsername);
        registry.add("spring.datasource.password", POSTGRES::getPassword);
        registry.add("spring.datasource.driver-class-name", () -> "org.postgresql.Driver");
    }
}
