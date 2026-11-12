package com.example.crudapp;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Loading the full context requires a reachable Oracle instance (see
 * application.properties). Point DB_URL/DB_USERNAME/DB_PASSWORD at a
 * real or Testcontainers-managed database before running this.
 */
@SpringBootTest
class OracleCrudServiceApplicationTests {

    @Test
    void contextLoads() {
    }

}
