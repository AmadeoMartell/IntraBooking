package jdbcTests;

import com.epam.capstone.util.database.ConnectionPool;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

class ConnectionPoolTest {
    private static final String DRIVER = "org.h2.Driver";
    private static final String URL    = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1";
    private static final String USER   = "sa";
    private static final String PASS   = "";

    @BeforeAll
    static void initDriver() throws ClassNotFoundException {
        Class.forName(DRIVER);
    }

    @Test
    @DisplayName("1. Base scenario: acquire and release connections")
    void testAcquireAndRelease() throws SQLException {
        ConnectionPool pool = new ConnectionPool(
                DRIVER, URL, USER, PASS,
                2,                       // maxPoolSize
                1,                       // minIdle
                Duration.ofMillis(1_000),
                Duration.ofMillis(5_000),
                Duration.ofMillis(5_000),
                Duration.ofSeconds(10)   // leakDetectionThreshold
        );

        Connection c1 = pool.getConnection();
        Connection c2 = pool.getConnection();
        assertNotNull(c1);
        assertNotNull(c2);

        assertThrows(SQLException.class, pool::getConnection);

        c1.close();
        Connection c3 = pool.getConnection();
        assertNotNull(c3);

        c2.close();
        c3.close();
        pool.shutdown();
    }

    @Test
    @DisplayName("2. Double close on same proxy does not break the pool")
    void testDoubleClose() throws SQLException {
        ConnectionPool pool = new ConnectionPool(
                DRIVER, URL, USER, PASS,
                1, 1,
                Duration.ofMillis(500),
                Duration.ofMillis(5_000),
                Duration.ofMillis(5_000),
                Duration.ofSeconds(10)
        );

        Connection c = pool.getConnection();
        c.close();
        assertDoesNotThrow(c::close);

        pool.shutdown();
    }

    @Test
    @DisplayName("3. Eviction of idle connections enforces minIdle")
    void testEvictionKeepsMinIdle() throws Exception {
        ConnectionPool pool = new ConnectionPool(
                DRIVER, URL, USER, PASS,
                3, 2,
                Duration.ofMillis(500),
                Duration.ofMillis(100),
                Duration.ofMillis(100),
                Duration.ofSeconds(10)
        );

        Connection first = pool.getConnection();
        first.close();
        Thread.sleep(300);

        Connection a = pool.getConnection();
        Connection b = pool.getConnection();
        assertNotNull(a);
        assertNotNull(b);

        Connection c = pool.getConnection();
        assertNotNull(c);
        assertEquals(3, pool.getTotalConnections());

        assertThrows(SQLException.class, pool::getConnection);

        a.close();
        b.close();
        c.close();
        pool.shutdown();
    }

    @Test
    @DisplayName("4. Concurrent access – no leaks or deadlocks")
    void testConcurrency() throws Exception {
        int threads  = 10;
        int poolSize = 5;
        ConnectionPool pool = new ConnectionPool(
                DRIVER, URL, USER, PASS,
                poolSize, 1,
                Duration.ofMillis(1_000),
                Duration.ofMillis(5_000),
                Duration.ofMillis(5_000),
                Duration.ofSeconds(10)
        );

        var workers = new java.util.ArrayList<Thread>();
        for (int i = 0; i < threads; i++) {
            Thread t = new Thread(() -> {
                try {
                    Connection cx = pool.getConnection();
                    Thread.sleep(50);
                    cx.close();
                } catch (Exception e) {
                    fail("Error in thread: " + e.getMessage());
                }
            }, "worker-" + i);
            workers.add(t);
            t.start();
        }
        for (Thread t : workers) {
            t.join();
        }

        pool.shutdown();
    }
}
