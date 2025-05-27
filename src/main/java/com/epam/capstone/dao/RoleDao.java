package com.epam.capstone.dao;

import com.epam.capstone.dao.rowmapper.RoleRowMapper;
import com.epam.capstone.model.Role;
import com.epam.capstone.util.database.CustomJdbcTemplate;
import org.intellij.lang.annotations.Language;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class RoleDao implements CrudDao<Role, Long> {

    @Language("SQL")
    private static final String FIND_BY_ID_SQL = "SELECT * FROM roles WHERE role_id = ?";
    @Language("SQL")
    private static final String FIND_ALL_SQL = "SELECT * FROM roles";
    @Language("SQL")
    private static final String INSERT_SQL = "INSERT INTO roles (name, description) VALUES (?, ?)";
    @Language("SQL")
    private static final String UPDATE_SQL = "UPDATE roles SET name = ?, description = ? WHERE role_id = ?";
    @Language("SQL")
    private static final String DELETE_SQL = "DELETE FROM roles WHERE role_id = ?";

    private final CustomJdbcTemplate jdbcTemplate;
    private final RoleRowMapper rowMapper;

    @Autowired
    public RoleDao(CustomJdbcTemplate jdbcTemplate) {
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
        jdbcTemplate.update(INSERT_SQL, role.getName(), role.getDescription());
    }

    @Override
    public void update(Role role) {
        jdbcTemplate.update(UPDATE_SQL, role.getName(), role.getDescription(), role.getRoleId());
    }

    @Override
    public void delete(Role role) {
        jdbcTemplate.update(DELETE_SQL, role.getRoleId());
    }
}
