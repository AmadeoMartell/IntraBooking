package com.epam.capstone.repository;

import com.epam.capstone.repository.dao.StatusDao;
import com.epam.capstone.repository.dao.rowmapper.StatusRowMapper;
import com.epam.capstone.model.Status;
import com.epam.capstone.repository.helper.QueryContainsMatcher;
import com.epam.capstone.util.database.CustomJdbcTemplate;
import lombok.extern.slf4j.Slf4j;
import org.intellij.lang.annotations.Language;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.Optional;

@Slf4j
@Repository
public class StatusRepository {

    private static final StatusRowMapper ROW_MAPPER = new StatusRowMapper();
    private static final SingleColumnRowMapper<Long> LONG_MAPPER =
            SingleColumnRowMapper.newInstance(Long.class);
    @Language("SQL")
    private static final String SELECT_BY_NAME = """
            SELECT status_id, name
              FROM statuses
             WHERE name = ?
            """;
    @Language("SQL")
    private static final String COUNT_BY_NAME_CONTAINS =
            "SELECT COUNT(*) FROM statuses WHERE name ILIKE ?";

    @Language("SQL")
    private static final String SELECT_PAGE_BY_NAME_CONTAINS = """
              SELECT status_id, name
                FROM statuses
               WHERE name ILIKE ?
            ORDER BY status_id
               LIMIT ? OFFSET ?
            """;
    private final StatusDao statusDao;
    private final CustomJdbcTemplate jdbc;

    public StatusRepository(StatusDao statusDao,
                            CustomJdbcTemplate jdbc) {
        this.statusDao = statusDao;
        this.jdbc = jdbc;
    }

    public Optional<Status> findById(Short id) {
        try {
            return Optional.ofNullable(statusDao.findById(id));
        } catch (RuntimeException e) {
            log.error("findById({}) failed", id, e);
            return Optional.empty();
        }
    }

    public boolean existsById(Short id) {
        try {
            return statusDao.findById(id) != null;
        } catch (RuntimeException e) {
            log.error("existsById({}) failed", id, e);
            return false;
        }
    }

    public long count() {
        try {
            return statusDao.findAll().size();
        } catch (RuntimeException e) {
            log.error("count() failed", e);
            return 0L;
        }
    }

    public Page<Status> findAll(Pageable pg) {
        try {
            return QueryContainsMatcher.pageList(statusDao.findAll(), pg);
        } catch (RuntimeException e) {
            log.error("findAll(Pageable) failed", e);
            return QueryContainsMatcher.pageList(Collections.emptyList(), pg);
        }
    }

    public Status save(Status s) {
        try {
            if (s.getStatusId() == null) {
                statusDao.save(s);
            } else {
                statusDao.update(s);
            }
            return s;
        } catch (RuntimeException e) {
            log.error("save({}) failed", s, e);
            throw new RuntimeException("Could not save Status", e);
        }
    }

    public void deleteById(Short id) {
        try {
            Status existing = statusDao.findById(id);
            if (existing != null) {
                statusDao.delete(existing);
            }
        } catch (RuntimeException e) {
            log.error("deleteById({}) failed", id, e);
            throw new RuntimeException("Could not delete Status", e);
        }
    }

    public Optional<Status> findByName(String name) {
        try {
            return Optional.of(jdbc.queryForObject(
                    SELECT_BY_NAME, ROW_MAPPER, name
            ));
        } catch (RuntimeException e) {
            log.error("findByName({}) failed", name, e);
            return Optional.empty();
        }
    }

    public Page<Status> findByNameContaining(String kw, Pageable pg) {
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
}
