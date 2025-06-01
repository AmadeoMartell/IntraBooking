package jdbcTests;

import com.epam.capstone.util.database.ConnectionPool;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ConnectionPoolDynamicTest {
    private static final String DRIVER = "org.h2.Driver";
    private static final String URL    = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1";
    private static final String USER   = "sa";
    private static final String PASS   = "";

    @BeforeAll
    static void initDriver() throws ClassNotFoundException {
        Class.forName(DRIVER);
    }

    @Test
    @DisplayName("5. Eviction by maxLifetime only (no idleTimeout)")
    void testEvictionByMaxLifetimeOnly() throws Exception {
        int MIN_IDLE = 2, MAX_POOL = 4;
        ConnectionPool pool = new ConnectionPool(
                DRIVER, URL, USER, PASS,
                MAX_POOL, MIN_IDLE,
                Duration.ofSeconds(1),      // maxLifetime
                Duration.ofSeconds(10),     // idleTimeout (long, effectively disabled)
                Duration.ofMillis(100),     // connectionTimeout
                Duration.ofSeconds(10)      // leakDetectionThreshold
        );

        List<Connection> tmp = new ArrayList<>();
        for (int i = 0; i < MIN_IDLE + 2; i++) {
            tmp.add(pool.getConnection());
        }
        assertEquals(MAX_POOL, pool.getTotalConnections());

        tmp.forEach(c -> { try { c.close(); } catch (Exception ignore) {} });
        Thread.sleep(200);

        assertEquals(MIN_IDLE, pool.getTotalConnections());
        pool.shutdown();
    }

    @Test
    @DisplayName("6. Eviction by idleTimeout only (no maxLifetime)")
    void testEvictionByIdleTimeoutOnly() throws Exception {
        int MIN_IDLE = 1, MAX_POOL = 3;
        ConnectionPool pool = new ConnectionPool(
                DRIVER, URL, USER, PASS,
                MAX_POOL, MIN_IDLE,
                Duration.ofSeconds(10),     // maxLifetime (long, effectively disabled)
                Duration.ofMillis(100),     // idleTimeout
                Duration.ofSeconds(10),     // connectionTimeout
                Duration.ofSeconds(10)      // leakDetectionThreshold
        );

        List<Connection> tmp = new ArrayList<>();
        for (int i = 0; i < MIN_IDLE + 2; i++) {
            tmp.add(pool.getConnection());
        }
        assertEquals(MAX_POOL, pool.getTotalConnections());

        tmp.forEach(c -> { try { c.close(); } catch (Exception ignore) {} });
        Thread.sleep(300);

        assertEquals(MIN_IDLE, pool.getTotalConnections());
        pool.shutdown();
    }
}
