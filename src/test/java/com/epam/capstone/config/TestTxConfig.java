package com.epam.capstone.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

/**
 * Test-only Spring configuration.
 *
 * 1) @PropertySource loads application-test.properties so DatabaseConfig can read H2 settings.
 * 2) @Import(DatabaseConfig.class) brings in:
 *      - DataSource (your custom ConnectionPool)
 *      - DataSourceTransactionManager
 *      - (Any other beans defined in DatabaseConfig)
 * 3) We expose a vanilla JdbcTemplate bean pointing at that DataSource.
 * 4) We create a StartupTableCreator to run the DDL for 'test_table'.
 * 5) @ComponentScan("com.epam.capstone.service.tx") ensures Spring finds TxTestService.
 */
@Configuration
@PropertySource("classpath:application-test.properties")
@Import(DatabaseConfig.class)
@ComponentScan(basePackages = "com.epam.capstone.service.tx")
public class TestTxConfig {

    /**
     * Standard JdbcTemplate that wraps the DataSource (ConnectionPool).
     */
    @Bean
    public JdbcTemplate jdbcTemplate(DataSource dataSource) {
        return new JdbcTemplate(dataSource);
    }

    /**
     * On startup, create the test_table so tests can run against it.
     */
    @Bean
    public StartupTableCreator startupTableCreator(JdbcTemplate jdbcTemplate) {
        return new StartupTableCreator(jdbcTemplate);
    }

    static class StartupTableCreator {
        public StartupTableCreator(JdbcTemplate jdbc) {
            jdbc.execute("""
                CREATE TABLE IF NOT EXISTS test_table (
                  id   INT PRIMARY KEY,
                  name VARCHAR(100)
                )
            """);
        }
    }
}
