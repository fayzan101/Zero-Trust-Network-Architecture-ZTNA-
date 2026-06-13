package com.yourname.zerotrust.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
@Profile("!test")
@Order(Ordered.HIGHEST_PRECEDENCE)
public class DatabaseSchemaFixRunner implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(DatabaseSchemaFixRunner.class);

    private final JdbcTemplate jdbcTemplate;

    public DatabaseSchemaFixRunner(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void run(ApplicationArguments args) {
        dropLegacyDeviceUserIdColumn();
    }

    private void dropLegacyDeviceUserIdColumn() {
        Boolean exists = jdbcTemplate.query(
                """
                SELECT EXISTS (
                    SELECT 1 FROM information_schema.columns
                    WHERE table_name = 'devices' AND column_name = 'user_id'
                )
                """,
                rs -> rs.next() && rs.getBoolean(1));

        if (Boolean.TRUE.equals(exists)) {
            jdbcTemplate.execute("ALTER TABLE devices DROP COLUMN user_id");
            log.info("Dropped legacy devices.user_id column (replaced by owner_id FK)");
        }
    }
}
