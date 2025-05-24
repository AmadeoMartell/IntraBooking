package com.epam.capstone.config;

import com.epam.capstone.config.database.ConnectionPool;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.time.Duration;

@Configuration
@EnableTransactionManagement
@Slf4j
public class DatabaseConfig {

    private final static int DEFAULT_MAX_POOL_SIZE = 4;
    private final static int DEFAULT_MAX_ACTIVE_POOL_SIZE = 2;
    private final static long DEFAULT_CONNECTION_TIMEOUT = 30000;
    private final static long DEFAULT_IDLE_TIMEOUT = 600000;
    private final static long DEFAULT_MAX_LIFETIME = 1800000;
    private static final long DEFAULT_LEAK_DETECTION_THRESHOLD = 300_000;

    private final ApplicationContext applicationContext;
    private final Environment environment;

    @Autowired
    public DatabaseConfig(ApplicationContext applicationContext, Environment environment) {
        this.applicationContext = applicationContext;
        this.environment = environment;
    }

    @Bean
    public JdbcTemplate jdbcTemplate(DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    @Bean
    public DataSource dataSource() throws SQLException {
        String driver = environment.getRequiredProperty("spring.datasource.driver-class-name");
        String url = environment.getRequiredProperty("spring.datasource.url");
        String user = environment.getRequiredProperty("spring.datasource.username");
        String pass = environment.getRequiredProperty("spring.datasource.password");

        int maxPool = environment.getProperty("app.datasource.max-pool", Integer.class, DEFAULT_MAX_POOL_SIZE);
        int minIdle = environment.getProperty("app.datasource.min-idle", Integer.class, DEFAULT_MAX_ACTIVE_POOL_SIZE);
        long connTO = environment.getProperty("app.datasource.connection-timeout", Long.class, DEFAULT_CONNECTION_TIMEOUT);
        long idleTO = environment.getProperty("app.datasource.idle-timeout", Long.class, DEFAULT_IDLE_TIMEOUT);
        long maxLife = environment.getProperty("app.datasource.max-lifetime", Long.class, DEFAULT_MAX_LIFETIME);
        long leakThreshold = environment.getProperty("app.datasource.leak-detection-threshold", Long.class, DEFAULT_LEAK_DETECTION_THRESHOLD);

        return new ConnectionPool(
                driver,
                url,
                user,
                pass,
                maxPool,
                minIdle,
                Duration.ofMillis(connTO),
                Duration.ofMillis(idleTO),
                Duration.ofMillis(maxLife),
                Duration.ofMillis(leakThreshold)
        );
    }

    @Bean
    public PlatformTransactionManager transactionManager(DataSource ds) {
        return new DataSourceTransactionManager(ds);
    }

}
