package com.epam.capstone.repository;

import com.epam.capstone.repository.dao.RoleDao;
import com.epam.capstone.repository.dao.rowmapper.RoleRowMapper;
import com.epam.capstone.model.Role;
import com.epam.capstone.repository.helper.QueryContainsMatcher;
import com.epam.capstone.util.database.CustomJdbcTemplate;
import lombok.extern.slf4j.Slf4j;
import org.intellij.lang.annotations.Language;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Slf4j
@Repository
public class RoleRepository {

    private static final RoleRowMapper ROW_MAPPER = new RoleRowMapper();
    private static final SingleColumnRowMapper<Long> LONG_MAPPER =
            SingleColumnRowMapper.newInstance(Long.class);
    @Language("SQL")
    private static final String SELECT_BY_NAME = """
            SELECT role_id, name, description
              FROM roles
             WHERE name = ?
            """;
    @Language("SQL")
    private static final String COUNT_BY_NAME_CONTAINS =
            "SELECT COUNT(*) FROM roles WHERE name ILIKE ?";

    @Language("SQL")
    private static final String SELECT_PAGE_BY_NAME_CONTAINS = """
              SELECT role_id, name, description
                FROM roles
               WHERE name ILIKE ?
            ORDER BY role_id
               LIMIT ? OFFSET ?
            """;
    @Language("SQL")
    private static final String COUNT_BY_DESCRIPTION_CONTAINS =
            "SELECT COUNT(*) FROM roles WHERE description ILIKE ?";
    @Language("SQL")
    private static final String SELECT_PAGE_BY_DESCRIPTION_CONTAINS = """
              SELECT role_id, name, description
                FROM roles
               WHERE description ILIKE ?
            ORDER BY role_id
               LIMIT ? OFFSET ?
            """;
    private final RoleDao roleDao;
    private final CustomJdbcTemplate jdbc;

    public RoleRepository(RoleDao roleDao, CustomJdbcTemplate jdbc) {
        this.roleDao = roleDao;
        this.jdbc = jdbc;
    }

    public Optional<Role> findById(Long id) {
        try {
            return Optional.ofNullable(roleDao.findById(id));
        } catch (RuntimeException e) {
            log.error("findById({}) failed", id, e);
            return Optional.empty();
        }
    }

    public boolean existsById(Long id) {
        try {
            return roleDao.findById(id) != null;
        } catch (RuntimeException e) {
            log.error("existsById({}) failed", id, e);
            return false;
        }
    }

    public long count() {
        try {
            return roleDao.findAll().size();
        } catch (RuntimeException e) {
            log.error("count() failed", e);
            return 0L;
        }
    }

    public List<Role> findAll() {
        try {
            return roleDao.findAll();
        } catch (RuntimeException e) {
            log.error("findAll({}) failed", e);
            return Collections.emptyList();
        }
    }

    public Page<Role> findAll(Pageable pg) {
        try {
            return QueryContainsMatcher.pageList(roleDao.findAll(), pg);
        } catch (RuntimeException e) {
            log.error("findAll(Pageable) failed", e);
            return QueryContainsMatcher.pageList(Collections.emptyList(), pg);
        }
    }


    public Role save(Role r) {
        try {
            if (r.getRoleId() == null) {
                roleDao.save(r);
            } else {
                roleDao.update(r);
            }
            return r;
        } catch (RuntimeException e) {
            log.error("save({}) failed", r, e);
            throw new RuntimeException("Could not save Role", e);
        }
    }

    public void deleteById(Long id) {
        try {
            Role existing = roleDao.findById(id);
            if (existing != null) {
                roleDao.delete(existing);
            }
        } catch (RuntimeException e) {
            log.error("deleteById({}) failed", id, e);
            throw new RuntimeException("Could not delete Role", e);
        }
    }

    public Optional<Role> findByName(String name) {
        try {
            return Optional.of(jdbc.queryForObject(
                    SELECT_BY_NAME, ROW_MAPPER, name
            ));
        } catch (RuntimeException e) {
            log.error("findByName({}) failed", name, e);
            return Optional.empty();
        }
    }

    public Page<Role> findByNameContaining(String kw, Pageable pg) {
        return QueryContainsMatcher.findByQueryContaining(
                jdbc,
                COUNT_BY_NAME_CONTAINS,
                SELECT_PAGE_BY_NAME_CONTAINS,
                LONG_MAPPER,
                ROW_MAPPER,
                kw,
                pg
        );
    }

    public Page<Role> findByDescriptionContaining(String kw, Pageable pg) {
        return QueryContainsMatcher.findByQueryContaining(
                jdbc,
                COUNT_BY_DESCRIPTION_CONTAINS,
                SELECT_PAGE_BY_DESCRIPTION_CONTAINS,
                LONG_MAPPER,
                ROW_MAPPER,
                kw,
                pg
        );
    }
}
