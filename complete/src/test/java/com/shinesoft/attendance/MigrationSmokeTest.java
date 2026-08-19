package com.shinesoft.attendance;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class MigrationSmokeTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void allMigrationsAreApplied() {
        Integer count = jdbcTemplate.queryForObject(
            """
            SELECT COUNT(*)
            FROM "flyway_schema_history"
            WHERE "success" = TRUE
              AND "version" IS NOT NULL
            """,
            Integer.class);

        assertEquals(2, count);
    }
}
