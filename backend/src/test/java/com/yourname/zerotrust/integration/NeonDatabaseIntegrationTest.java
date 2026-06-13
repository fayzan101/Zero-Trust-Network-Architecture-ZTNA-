package com.yourname.zerotrust.integration;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.sql.Connection;

import javax.sql.DataSource;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;

@Tag("neon")
@EnabledIfEnvironmentVariable(named = "NEON_URL", matches = ".+")
@SpringBootTest
@ActiveProfiles("test")
class NeonDatabaseIntegrationTest {

    @DynamicPropertySource
    static void neonProperties(DynamicPropertyRegistry registry) {
        String url = System.getenv("NEON_URL");
        String user = System.getenv().getOrDefault("NEON_USERNAME", "postgres");
        String password = System.getenv().getOrDefault("NEON_PASSWORD", "");
        registry.add("spring.datasource.url", () -> url);
        registry.add("spring.datasource.username", () -> user);
        registry.add("spring.datasource.password", () -> password);
        registry.add("spring.datasource.driver-class-name", () -> "org.postgresql.Driver");
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "validate");
        registry.add("spring.sql.init.mode", () -> "never");
    }

    @Autowired
    private DataSource dataSource;

    @Test
    void connectsToNeonDatabase() throws Exception {
        try (Connection connection = dataSource.getConnection()) {
            assertNotNull(connection);
            assertTrue(connection.isValid(5));
        }
    }
}
