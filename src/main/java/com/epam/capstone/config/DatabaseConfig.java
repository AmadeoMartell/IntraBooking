package com.epam.capstone.config;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
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

@Configuration
@EnableTransactionManagement
public class DatabaseConfig {

    private final ApplicationContext applicationContext;
    private final Environment environment;

    private final int DEFAULT_MAX_POOL_SIZE = 4;
    private final int DEFAULT_MAX_ACTIVE_POOL_SIZE = 2;
    private final int DEFAULT_CONNECTION_TIMEOUT = 30000;
    private final int DEFAULT_IDLE_TIMEOUT = 600000;
    private final int DEFAULT_MAX_LIFETIME = 1800000;

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
    public DataSource dataSource() {
        var cfg = new HikariConfig();
        cfg.setDriverClassName(environment.getProperty("spring.datasource.driver-class-name"));
        cfg.setJdbcUrl(environment.getProperty("spring.datasource.url"));
        cfg.setUsername(environment.getProperty("spring.datasource.username"));
        cfg.setPassword(environment.getProperty("spring.datasource.password"));

        cfg.setMaximumPoolSize(environment.getProperty("spring.datasource.hikari.maximum-pool-size",
                Integer.class, DEFAULT_MAX_POOL_SIZE));
        cfg.setMinimumIdle(environment.getProperty("spring.datasource.hikari.minimum-idle",
                Integer.class, DEFAULT_MAX_ACTIVE_POOL_SIZE));
        cfg.setConnectionTimeout(environment.getProperty("spring.datasource.hikari.connection-timeout",
                Integer.class, DEFAULT_CONNECTION_TIMEOUT));
        cfg.setIdleTimeout(environment.getProperty("spring.datasource.hikari.idle-timeout",
                Integer.class, DEFAULT_IDLE_TIMEOUT));
        cfg.setMaxLifetime(environment.getProperty("spring.datasource.hikari.max-lifetime",
                Integer.class, DEFAULT_MAX_LIFETIME));
        return new HikariDataSource(cfg);
    }

    @Bean
    public PlatformTransactionManager transactionManager(DataSource ds) {
        return new DataSourceTransactionManager(ds);
    }
}
