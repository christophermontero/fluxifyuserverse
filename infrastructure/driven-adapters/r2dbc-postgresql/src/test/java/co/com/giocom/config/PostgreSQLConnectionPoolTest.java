package co.com.giocom.config;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class PostgreSQLConnectionPoolTest {

    @Test
    void getConnectionConfig() {
        PostgreSQLConnectionPool postgreSQLConnectionPool = new PostgreSQLConnectionPool();
        Assertions.assertNotNull(
                postgreSQLConnectionPool.getConnectionConfig());
    }
}
