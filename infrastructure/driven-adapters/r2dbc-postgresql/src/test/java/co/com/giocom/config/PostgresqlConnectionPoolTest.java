package co.com.giocom.config;

import io.r2dbc.spi.ConnectionFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(MockitoExtension.class)
class PostgresqlConnectionPoolTest {

    @InjectMocks
    private PostgresqlConnectionPool connectionPool;

    @BeforeEach
    void setUp() {
        connectionPool = new PostgresqlConnectionPool();
    }

    @Test
    void verifyPostgresqlConnectionConfiguration() {

        PostgresqlConnectionProperties properties = new PostgresqlConnectionProperties();
        properties.setDatabase("test");
        properties.setSchema("testschema");
        properties.setUsername("usertest");
        properties.setPassword("userpass");
        properties.setHost("localhost");
        properties.setPort(1);

        ConnectionFactory connectionFactory = connectionPool.getConnectionConfig(properties);

        assertNotNull(connectionFactory);
    }

}
