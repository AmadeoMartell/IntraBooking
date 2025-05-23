package com.epam.capstone.dao;

import com.epam.capstone.mapper.row.UserRowMapper;
import com.epam.capstone.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class UserDao implements CrudDao<User, Long>{

    private static final String FIND_BY_ID_SQL = "SELECT * FROM users WHERE user_id = ?";
    private static final String FIND_ALL_SQL = "SELECT * FROM users";
    private static final String INSERT_SQL = "INSERT INTO users " +
            "(role_id, username, password_hash, full_name, email, phone)" +
            " VALUES (?, ?, ?, ?, ?, ?)";
    private static final String UPDATE_SQL = "UPDATE users " +
            "SET role_id = ?, username = ?, password_hash = ?, full_name = ?, email = ?, phone = ?" +
            " WHERE user_id = ?";
    private static final String DELETE_SQL = "DELETE FROM users WHERE user_id = ?";

    private final JdbcTemplate jdbcTemplate;
    private final UserRowMapper rowMapper;

    @Autowired
    public UserDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.rowMapper = new UserRowMapper();
    }

    @Override
    public User findById(Long id) {
        return jdbcTemplate.queryForObject(FIND_BY_ID_SQL, rowMapper, id);
    }

    @Override
    public List<User> findAll() {
        return jdbcTemplate.query(FIND_ALL_SQL, rowMapper);
    }

    @Override
    public void save(User user) {
        jdbcTemplate.update(INSERT_SQL,
                user.getRoleID(),
                user.getUsername(),
                user.getPasswordHash(),
                user.getFullName(),
                user.getEmail(),
                user.getPhone());
    }

    @Override
    public void update(User user) {
        jdbcTemplate.update(UPDATE_SQL,
                user.getRoleID(),
                user.getUsername(),
                user.getPasswordHash(),
                user.getFullName(),
                user.getEmail(),
                user.getPhone(),
                user.getUserId());
    }

    @Override
    public void delete(User user) {
        jdbcTemplate.update(DELETE_SQL, user.getUserId());
    }
}
