package com.epam.capstone.dao;

import com.epam.capstone.mapper.row.RoleRowMapper;
import com.epam.capstone.model.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class RoleDao implements CrudDao<Role, Long> {

    private static final String FIND_BY_ID_SQL =
            "SELECT * FROM roles WHERE role_id = ?";
    private static final String FIND_ALL_SQL =
            "SELECT * FROM roles";
    private static final String INSERT_SQL =
            "INSERT INTO roles (name, description) VALUES (?, ?)";
    private static final String UPDATE_SQL =
            "UPDATE roles SET name = ?, description = ? WHERE role_id = ?";
    private static final String DELETE_SQL =
            "DELETE FROM roles WHERE role_id = ?";

    private final JdbcTemplate jdbcTemplate;
    private final RoleRowMapper rowMapper;

    @Autowired
    public RoleDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.rowMapper = new RoleRowMapper();
    }

    @Override
    public Role findById(Long id) {
        return jdbcTemplate.queryForObject(FIND_BY_ID_SQL, rowMapper, id);
    }

    @Override
    public List<Role> findAll() {
        return jdbcTemplate.query(FIND_ALL_SQL, rowMapper);
    }

    @Override
    public void save(Role role) {
        jdbcTemplate.update(INSERT_SQL,
                role.getName(),
                role.getDescription());
    }

    @Override
    public void update(Role role) {
        jdbcTemplate.update(UPDATE_SQL,
                role.getName(),
                role.getDescription(),
                role.getRoleId());
    }

    @Override
    public void delete(Role role) {
        jdbcTemplate.update(DELETE_SQL, role.getRoleId());
    }
}
