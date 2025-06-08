package com.epam.capstone.repository;

import com.epam.capstone.repository.dao.RoomTypeDao;
import com.epam.capstone.repository.dao.rowmapper.RoomTypeRowMapper;
import com.epam.capstone.model.RoomType;
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
public class RoomTypeRepository {

    private static final RoomTypeRowMapper ROW_MAPPER =
            new RoomTypeRowMapper();
    private static final SingleColumnRowMapper<Long> LONG_MAPPER =
            SingleColumnRowMapper.newInstance(Long.class);
    @Language("SQL")
    private static final String SELECT_BY_NAME = """
            SELECT type_id, name, capacity, description
              FROM room_types
             WHERE name = ?
            """;
    @Language("SQL")
    private static final String COUNT_BY_NAME_CONTAINS =
            "SELECT COUNT(*) FROM room_types WHERE name ILIKE ?";

    @Language("SQL")
    private static final String SELECT_PAGE_BY_NAME_CONTAINS = """
              SELECT type_id, name, capacity, description
                FROM room_types
               WHERE name ILIKE ?
            ORDER BY type_id
               LIMIT ? OFFSET ?
            """;
    @Language("SQL")
    private static final String COUNT_BY_CAPACITY =
            "SELECT COUNT(*) FROM room_types WHERE capacity = ?";
    @Language("SQL")
    private static final String SELECT_PAGE_BY_CAPACITY = """
              SELECT type_id, name, capacity, description
                FROM room_types
               WHERE capacity = ?
            ORDER BY type_id
               LIMIT ? OFFSET ?
            """;
    @Language("SQL")
    private static final String COUNT_BY_CAPACITY_GE =
            "SELECT COUNT(*) FROM room_types WHERE capacity >= ?";
    @Language("SQL")
    private static final String SELECT_PAGE_BY_CAPACITY_GE = """
              SELECT type_id, name, capacity, description
                FROM room_types
               WHERE capacity >= ?
            ORDER BY type_id
               LIMIT ? OFFSET ?
            """;
    @Language("SQL")
    private static final String SELECT_TYPES_FOR_LOCATION = """
        SELECT rt.type_id,
               rt.name,
               rt.capacity,
               rt.description
          FROM room_types rt
          JOIN rooms r
            ON rt.type_id = r.type_id
         WHERE r.location_id = ?
         GROUP BY rt.type_id, rt.name, rt.capacity, rt.description
         ORDER BY rt.name
        """;

    private final RoomTypeDao roomTypeDao;
    private final CustomJdbcTemplate jdbc;

    public RoomTypeRepository(RoomTypeDao roomTypeDao,
                              CustomJdbcTemplate jdbc) {
        this.roomTypeDao = roomTypeDao;
        this.jdbc = jdbc;
    }

    public Optional<RoomType> findById(Integer id) {
        try {
            return Optional.ofNullable(roomTypeDao.findById(id));
        } catch (RuntimeException e) {
            log.error("findById({}) failed", id, e);
            return Optional.empty();
        }
    }
    /**
     * Returns all RoomType entities that have at least one room in the given location.
     *
     * @param locationId ID of the location
     * @return a List of RoomType (entity) objects
     */
    public List<RoomType> findByLocationId(Long locationId) {
        try {
            return jdbc.query(SELECT_TYPES_FOR_LOCATION, ROW_MAPPER, locationId);
        } catch (Exception ex) {
            return Collections.emptyList();
        }
    }
    public boolean existsById(Integer id) {
        try {
            return roomTypeDao.findById(id) != null;
        } catch (RuntimeException e) {
            log.error("existsById({}) failed", id, e);
            return false;
        }
    }

    public long count() {
        try {
            return roomTypeDao.findAll().size();
        } catch (RuntimeException e) {
            log.error("count() failed", e);
            return 0L;
        }
    }

    public List<RoomType> findAll() {
        try {
            return roomTypeDao.findAll();
        } catch (RuntimeException e) {
            log.error("findAll() failed", e);
            return Collections.emptyList();
        }
    }

    public Page<RoomType> findAll(Pageable pg) {
        try {
            return QueryContainsMatcher.pageList(roomTypeDao.findAll(), pg);
        } catch (RuntimeException e) {
            log.error("findAll(Pageable) failed", e);
            return QueryContainsMatcher.pageList(Collections.emptyList(), pg);
        }
    }

    public RoomType save(RoomType rt) {
        try {
            if (rt.getTypeId() == null) {
                roomTypeDao.save(rt);
            } else {
                roomTypeDao.update(rt);
            }
            return rt;
        } catch (RuntimeException e) {
            log.error("save({}) failed", rt, e);
            throw new RuntimeException("Could not save RoomType", e);
        }
    }

    public void deleteById(Integer id) {
        try {
            RoomType existing = roomTypeDao.findById(id);
            if (existing != null) {
                roomTypeDao.delete(existing);
            }
        } catch (RuntimeException e) {
            log.error("deleteById({}) failed", id, e);
            throw new RuntimeException("Could not delete RoomType", e);
        }
    }

    public Optional<RoomType> findByName(String name) {
        try {
            return Optional.of(jdbc.queryForObject(
                    SELECT_BY_NAME, ROW_MAPPER, name
            ));
        } catch (RuntimeException e) {
            log.error("findByName({}) failed", name, e);
            return Optional.empty();
        }
    }

    public Page<RoomType> findByNameContaining(String kw, Pageable pg) {
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

    public Page<RoomType> findByCapacity(int cap, Pageable pg) {
        try {
            Long total = jdbc.queryForObject(
                    COUNT_BY_CAPACITY, LONG_MAPPER, cap
            );
            List<RoomType> list = jdbc.query(
                    SELECT_PAGE_BY_CAPACITY, ROW_MAPPER,
                    cap, pg.getPageSize(), pg.getOffset()
            );
            return new PageImpl<>(list, pg, total != null ? total : 0L);
        } catch (RuntimeException e) {
            log.error("findByCapacity({}, {}) failed", cap, pg, e);
            return new PageImpl<>(Collections.emptyList(), pg, 0);
        }
    }

    public Page<RoomType> findByCapacityGreaterThanEqual(int cap, Pageable pg) {
        try {
            Long total = jdbc.queryForObject(
                    COUNT_BY_CAPACITY_GE, LONG_MAPPER, cap
            );
            List<RoomType> list = jdbc.query(
                    SELECT_PAGE_BY_CAPACITY_GE, ROW_MAPPER,
                    cap, pg.getPageSize(), pg.getOffset()
            );
            return new PageImpl<>(list, pg, total != null ? total : 0L);
        } catch (RuntimeException e) {
            log.error("findByCapacityGreaterThanEqual({}, {}) failed", cap, pg, e);
            return new PageImpl<>(Collections.emptyList(), pg, 0);
        }
    }
}
