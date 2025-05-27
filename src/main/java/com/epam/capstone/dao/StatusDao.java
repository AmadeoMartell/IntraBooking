package com.epam.capstone.dao;

import com.epam.capstone.dao.rowmapper.StatusRowMapper;
import com.epam.capstone.model.Status;
import com.epam.capstone.util.database.CustomJdbcTemplate;
import org.intellij.lang.annotations.Language;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;


import java.util.List;

@Repository
public class StatusDao implements CrudDao<Status, Short> {

    @Language("SQL")
    private static final String FIND_BY_ID_SQL = "SELECT * FROM statuses WHERE status_id = ?";
    @Language("SQL")
    private static final String FIND_ALL_SQL = "SELECT * FROM statuses";
    @Language("SQL")
    private static final String INSERT_SQL = "INSERT INTO statuses (status_id, name) VALUES (?, ?)";
    @Language("SQL")
    private static final String UPDATE_SQL = "UPDATE statuses SET name = ? WHERE status_id = ?";
    @Language("SQL")
    private static final String DELETE_SQL = "DELETE FROM statuses WHERE status_id = ?";

    private final CustomJdbcTemplate jdbcTemplate;
    private final StatusRowMapper rowMapper;

    @Autowired
    public StatusDao(CustomJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.rowMapper = new StatusRowMapper();
    }

    @Override
    public Status findById(Short id) {
        return jdbcTemplate.queryForObject(FIND_BY_ID_SQL, rowMapper, id);
    }

    @Override
    public List<Status> findAll() {
        return jdbcTemplate.query(FIND_ALL_SQL, rowMapper);
    }

    @Override
    public void save(Status status) {
        jdbcTemplate.update(INSERT_SQL, status.getStatusId(), status.getName());
    }

    @Override
    public void update(Status status) {
        jdbcTemplate.update(UPDATE_SQL, status.getName(), status.getStatusId());
    }

    @Override
    public void delete(Status status) {
        jdbcTemplate.update(DELETE_SQL, status.getStatusId());
    }
}
