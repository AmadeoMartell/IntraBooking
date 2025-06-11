package com.epam.capstone.service.tx;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service for testing transaction behavior. Uses the standard JdbcTemplate,
 * but the DataSource is your custom ConnectionPool (configured via DatabaseConfig).
 */
@Service
public class TxTestService {

    private final JdbcTemplate jdbc;

    public TxTestService(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    /**
     * Test method: inserts a single row, then throws an unchecked exception.
     * Should fully roll back the transaction (insert → rollback).
     */
    @Transactional
    public void insertThenFail(int id, String name) {
        jdbc.update("INSERT INTO test_table(id, name) VALUES (?, ?)", id, name);
        throw new IllegalStateException("Intentional failure to test rollback");
    }

    /**
     * Transactional method that inserts two rows and completes successfully.
     * Both INSERTs should be committed.
     */
    @Transactional
    public void insertTwoAndSucceed(int id1, String name1, int id2, String name2) {
        jdbc.update("INSERT INTO test_table(id, name) VALUES (?, ?)", id1, name1);
        jdbc.update("INSERT INTO test_table(id, name) VALUES (?, ?)", id2, name2);
        // no exceptions → commit
    }

    /**
     * Transactional method that inserts two rows, then throws an exception.
     * Should fully roll back both inserts.
     */
    @Transactional
    public void insertTwoThenFail(int id1, String name1, int id2, String name2) {
        jdbc.update("INSERT INTO test_table(id, name) VALUES (?, ?)", id1, name1);
        jdbc.update("INSERT INTO test_table(id, name) VALUES (?, ?)", id2, name2);
        throw new IllegalArgumentException("Intentional failure after two inserts");
    }

    /**
     * Non-transactional method that simply returns the current row count.
     * Will be called outside of any transaction.
     */
    public int countRows() {
        return jdbc.queryForObject("SELECT COUNT(*) FROM test_table", Integer.class);
    }
}
