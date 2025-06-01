package jdbcTests;

import com.epam.capstone.util.database.ConnectionPool;
import org.junit.jupiter.api.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ConnectionPoolExtendedTest {
    private static final String DRIVER = "org.h2.Driver";
    private static final String URL    = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1";
    private static final String USER   = "sa";
    private static final String PASS   = "";

    @BeforeAll
    static void initDriver() throws ClassNotFoundException {
        Class.forName(DRIVER);
    }

    @Test
    @DisplayName("7. Operations after shutdown should throw SQLException")
    void testAfterShutdownOperationsFail() throws Exception {
        ConnectionPool pool = new ConnectionPool(
                DRIVER, URL, USER, PASS,
                2, 1,
                Duration.ofMillis(500),
                Duration.ofMillis(5_000),
                Duration.ofMillis(5_000),
                Duration.ofSeconds(10)
        );

        Connection c = pool.getConnection();
        pool.shutdown();

        assertThrows(SQLException.class, () -> c.createStatement());
    }

    @Test
    @DisplayName("8. Concurrent dynamic growth of pool up to maxPoolSize")
    void testConcurrentDynamicGrow() throws Exception {
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

        List<Thread> workers = new ArrayList<>();
        for (int i = 0; i < threads; i++) {
            Thread t = new Thread(() -> {
                try {
                    Connection cx = pool.getConnection();
                    Thread.sleep(50);
                    cx.close();
                } catch (Exception e) {
                    fail("Error in thread: " + e.getMessage());
                }
            });
            workers.add(t);
            t.start();
        }
        for (Thread t : workers) {
            t.join();
        }

        assertEquals(poolSize, pool.getTotalConnections());
        pool.shutdown();
    }
}
