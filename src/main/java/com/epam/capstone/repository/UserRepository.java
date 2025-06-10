package com.epam.capstone.repository;

import com.epam.capstone.repository.dao.UserDao;
import com.epam.capstone.repository.dao.rowmapper.UserRowMapper;
import com.epam.capstone.model.User;
import com.epam.capstone.repository.helper.QueryContainsMatcher;
import com.epam.capstone.util.database.CustomJdbcTemplate;
import lombok.extern.slf4j.Slf4j;
import org.intellij.lang.annotations.Language;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Slf4j
@Repository
public class UserRepository {

    private static final UserRowMapper ROW_MAPPER = new UserRowMapper();
    private static final SingleColumnRowMapper<Long> LONG_MAPPER =
            SingleColumnRowMapper.newInstance(Long.class);

    @Language("SQL")
    private static final String SELECT_BY_USERNAME = """
            SELECT user_id, role_id, username, password_hash,
                   full_name, email, phone,
                   created_at, updated_at
              FROM users
             WHERE username = ?
            """;
    @Language("SQL")
    private static final String SELECT_BY_EMAIL = """
            SELECT user_id, role_id, username, password_hash,
                   full_name, email, phone,
                   created_at, updated_at
              FROM users
             WHERE email = ?
            """;
    @Language("SQL")
    private static final String COUNT_BY_FULLNAME =
            "SELECT COUNT(*) FROM users WHERE full_name ILIKE ?";
    @Language("SQL")
    private static final String SELECT_PAGE_BY_FULLNAME = """
              SELECT user_id, role_id, username, password_hash,
                     full_name, email, phone,
                     created_at, updated_at
                FROM users
               WHERE full_name ILIKE ?
            ORDER BY user_id
               LIMIT ? OFFSET ?
            """;
    @Language("SQL")
    private static final String COUNT_BY_ROLE =
            "SELECT COUNT(*) FROM users WHERE role_id = ?";
    @Language("SQL")
    private static final String SELECT_PAGE_BY_ROLE = """
              SELECT user_id, role_id, username, password_hash,
                     full_name, email, phone,
                     created_at, updated_at
                FROM users
               WHERE role_id = ?
            ORDER BY user_id
               LIMIT ? OFFSET ?
            """;
    @Language("SQL")
    private static final String SELECT_BY_USERNAME_OR_EMAIL = """
    SELECT user_id, role_id, username, password_hash,
           full_name, email, phone,
           created_at, updated_at
      FROM users
     WHERE username = ?
        OR email    = ?
    """;
    private final UserDao userDao;
    private final CustomJdbcTemplate jdbc;

    public UserRepository(UserDao userDao, CustomJdbcTemplate jdbc) {
        this.userDao = userDao;
        this.jdbc = jdbc;
    }

    public Optional<User> selectByUsernameOrEmail(String username, String email) {
        try {
            var user = jdbc.queryForObject(
                    SELECT_BY_USERNAME_OR_EMAIL, ROW_MAPPER, username, email);
            return Optional.ofNullable(user);
        } catch (RuntimeException e) {
            log.error("selectByUsernameOrEmail({} {}) failed", username, email, e);
            return Optional.empty();
        }
    }

    public Optional<User> findById(Long id) {
        try {
            return Optional.ofNullable(userDao.findById(id));
        } catch (RuntimeException e) {
            log.error("findById({}) failed", id, e);
            return Optional.empty();
        }
    }

    public boolean existsById(Long id) {
        try {
            return userDao.findById(id) != null;
        } catch (RuntimeException e) {
            log.error("existsById({}) failed", id, e);
            return false;
        }
    }

    public long count() {
        try {
            return userDao.findAll().size();
        } catch (RuntimeException e) {
            log.error("count() failed", e);
            return 0L;
        }
    }

    public Page<User> findAll(Pageable pg) {
        try {
            return QueryContainsMatcher.pageList(userDao.findAll(), pg);
        } catch (RuntimeException e) {
            log.error("findAll(Pageable) failed", e);
            return QueryContainsMatcher.pageList(Collections.emptyList(), pg);
        }
    }

    public User save(User u) {
        try {
            if (u.getUserId() == null) {
                userDao.save(u);
            } else {
                userDao.update(u);
            }
            return u;
        } catch (RuntimeException e) {
            log.error("save({}) failed", u, e);
            throw new RuntimeException("Could not save User", e);
        }
    }

    public void deleteById(Long id) {
        try {
            User existing = userDao.findById(id);
            if (existing != null) {
                userDao.delete(existing);
            }
        } catch (RuntimeException e) {
            log.error("deleteById({}) failed", id, e);
            throw new RuntimeException("Could not delete User", e);
        }
    }

    public Optional<User> findByUsername(String username) {
        try {
            return Optional.of(jdbc.queryForObject(
                    SELECT_BY_USERNAME, ROW_MAPPER, username
            ));
        } catch (RuntimeException e) {
            log.error("findByUsername({}) failed", username, e);
            return Optional.empty();
        }
    }

    public Optional<User> findByEmail(String email) {
        try {
            return Optional.of(jdbc.queryForObject(
                    SELECT_BY_EMAIL, ROW_MAPPER, email
            ));
        } catch (RuntimeException e) {
            log.error("findByEmail({}) failed", email, e);
            return Optional.empty();
        }
    }

    public Page<User> findByFullNameContaining(String kw, Pageable pg) {
        return QueryContainsMatcher.findByQueryContaining(
                jdbc,
                COUNT_BY_FULLNAME,
                SELECT_PAGE_BY_FULLNAME,
                LONG_MAPPER,
                ROW_MAPPER,
                kw,
                pg
        );
    }

    public Page<User> findByRoleId(Long roleId, Pageable pg) {
        try {
            Long total = jdbc.queryForObject(
                    COUNT_BY_ROLE, LONG_MAPPER, roleId
            );
            List<User> list = jdbc.query(
                    SELECT_PAGE_BY_ROLE, ROW_MAPPER,
                    roleId, pg.getPageSize(), pg.getOffset()
            );
            return new PageImpl<>(list, pg, total != null ? total : 0L);
        } catch (RuntimeException e) {
            log.error("findByRoleId({}, {}) failed", roleId, pg, e);
            return new PageImpl<>(Collections.emptyList(), pg, 0);
        }
    }
}
