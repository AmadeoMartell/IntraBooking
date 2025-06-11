package com.epam.capstone.service.tx;

import com.epam.capstone.config.TestTxConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import javax.sql.DataSource;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Extended integration tests to verify transactional behavior:
 * - rollback on exception in the middle of a transaction
 * - commit on a successful transaction
 * - rollback of multiple operations when an error occurs after several INSERTs
 *
 * Before each test we explicitly clear the table via JdbcTemplate to ensure it is empty.
 */
@SpringJUnitConfig
@ContextConfiguration(classes = TestTxConfig.class)
@TestPropertySource("classpath:application-test.properties")
class TransactionalRollbackTest {
    @Autowired
    private DataSource dataSource;

    @Autowired
    private TxTestService txTestService;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @BeforeEach
    void cleanTable() {
        // Delete all rows from test_table before each test
        jdbcTemplate.update("DELETE FROM test_table");
        // Verify that the table is indeed empty
        int rows = txTestService.countRows();
        assertEquals(0, rows, "Before each test, test_table should be empty");
    }

    @Test
    @DisplayName("insertThenFail() should roll back a single insert on exception")
    void testInsertThenFailIsRolledBack() {
        // 1) Table is empty (see cleanTable)

        // 2) Call the method that inserts one row and then throws an exception
        IllegalStateException ex = assertThrows(
                IllegalStateException.class,
                () -> txTestService.insertThenFail(1, "one"),
                "Expected IllegalStateException from insertThenFail"
        );
        assertTrue(ex.getMessage().contains("Intentional failure"),
                "Exception message should contain 'Intentional failure'");

        // 3) After rollback, the table should remain empty
        assertEquals(0, txTestService.countRows(),
                "After rollback the table should remain empty");
    }

    @Test
    @DisplayName("insertTwoAndSucceed() should commit both inserts successfully")
    void testInsertTwoAndSucceedCommitsBoth() {
        // 1) Table is empty

        // 2) Method inserts two rows and does not throw an exception
        txTestService.insertTwoAndSucceed(10, "alpha", 20, "beta");

        // 3) After commit, both rows should be in the table
        assertEquals(2, txTestService.countRows(),
                "After a successful transaction there should be 2 records in the table");
    }

    @Test
    @DisplayName("verify DataSource is using our custom connection pool")
    void testUsingCustomConnectionPool() {
        String actualClassName = dataSource.getClass().getName();
        System.out.println("DataSource class = " + actualClassName);
        // Expect the class name to contain 'ConnectionPool'
        assertTrue(
                actualClassName.contains("ConnectionPool"),
                "Expected DataSource class name to contain 'ConnectionPool', but was: " + actualClassName
        );
    }

    @Test
    @DisplayName("insertTwoThenFail() should roll back both inserts on failure")
    void testInsertTwoThenFailRollsBackBoth() {
        // 1) Table is empty

        // 2) Call the method that inserts two rows and then throws an exception
        IllegalArgumentException ex = assertThrows(
                IllegalArgumentException.class,
                () -> txTestService.insertTwoThenFail(100, "first", 200, "second"),
                "Expected IllegalArgumentException from insertTwoThenFail"
        );
        assertTrue(ex.getMessage().contains("Intentional failure after two"),
                "Exception message should contain 'Intentional failure after two'");

        // 3) After rollback, both inserts should be undone and the table empty
        assertEquals(0, txTestService.countRows(),
                "After rollback both inserts should be undone, leaving the table empty");
    }

    @Test
    @DisplayName("Two consecutive successful insertTwoAndSucceed calls should result in 4 rows")
    void testMultipleSuccessCallsAccumulateRows() {
        // 1) Table is empty

        // 2) First successful call
        txTestService.insertTwoAndSucceed(11, "x", 22, "y");
        assertEquals(2, txTestService.countRows(),
                "After the first successful call there should be 2 records");

        // 3) Second successful call
        txTestService.insertTwoAndSucceed(33, "u", 44, "v");
        assertEquals(4, txTestService.countRows(),
                "After the second successful call there should be 4 records");
    }
}
