package com.epam.capstone.repository.dao;

import com.epam.capstone.repository.dao.rowmapper.UserRowMapper;
import com.epam.capstone.model.User;
import com.epam.capstone.util.database.CustomJdbcTemplate;
import lombok.extern.slf4j.Slf4j;
import org.intellij.lang.annotations.Language;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
@Slf4j
public class UserDao implements CrudDao<User, Long> {

    @Language("SQL")
    private static final String FIND_BY_ID_SQL = "SELECT * FROM users WHERE user_id = ?";
    @Language("SQL")
    private static final String FIND_ALL_SQL = "SELECT * FROM users";
    @Language("SQL")
    private static final String INSERT_SQL =
            "INSERT INTO users (role_id, username, password_hash, full_name, email, phone) VALUES (?, ?, ?, ?, ?, ?)";
    @Language("SQL")
    private static final String UPDATE_SQL =
            "UPDATE users SET role_id = ?, username = ?, password_hash = ?, full_name = ?, email = ?, phone = ?, updated_at = ? WHERE user_id = ?";
    @Language("SQL")
    private static final String DELETE_SQL = "DELETE FROM users WHERE user_id = ?";
    @Language("SQL")
    private static final String FIND_BY_USERNAME_SQL = "SELECT * FROM users WHERE username = ?";

    private final CustomJdbcTemplate jdbcTemplate;
    private final UserRowMapper rowMapper;

    @Autowired
    public UserDao(CustomJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.rowMapper = new UserRowMapper();
    }

    public User findByUsername(String username) {
        log.debug("Finding user by Username {}", username);
        return jdbcTemplate.queryForObject(FIND_BY_USERNAME_SQL, rowMapper, username);
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
        jdbcTemplate.update(INSERT_SQL, user.getRoleId(), user.getUsername(), user.getPasswordHash(), user.getFullName(), user.getEmail(), user.getPhone());
    }

    @Override
    public void update(User user) {
        jdbcTemplate.update(UPDATE_SQL, user.getRoleId(), user.getUsername(), user.getPasswordHash(), user.getFullName(), user.getEmail(), user.getPhone(), user.getUserId(), LocalDateTime.now());
    }

    @Override
    public void delete(User user) {
        jdbcTemplate.update(DELETE_SQL, user.getUserId());
    }
}
