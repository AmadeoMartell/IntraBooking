package com.epam.capstone.repository;

import com.epam.capstone.repository.dao.RoomDao;
import com.epam.capstone.repository.dao.rowmapper.RoomRowMapper;
import com.epam.capstone.model.Room;
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
public class RoomRepository {

    private static final RoomRowMapper ROW_MAPPER = new RoomRowMapper();
    private static final SingleColumnRowMapper<Long> LONG_MAPPER =
            SingleColumnRowMapper.newInstance(Long.class);
    @Language("SQL")
    private static final String SELECT_BY_NAME = """
            SELECT room_id, location_id, type_id, name, capacity, description
              FROM rooms
             WHERE name = ?
            """;
    @Language("SQL")
    private static final String COUNT_BY_NAME_CONTAINS =
            "SELECT COUNT(*) FROM rooms WHERE name ILIKE ?";

    @Language("SQL")
    private static final String SELECT_PAGE_BY_NAME_CONTAINS = """
              SELECT room_id, location_id, type_id, name, capacity, description
                FROM rooms
               WHERE name ILIKE ?
            ORDER BY room_id
               LIMIT ? OFFSET ?
            """;
    @Language("SQL")
    private static final String COUNT_BY_LOCATION =
            "SELECT COUNT(*) FROM rooms WHERE location_id = ?";
    @Language("SQL")
    private static final String SELECT_PAGE_BY_LOCATION = """
              SELECT room_id, location_id, type_id, name, capacity, description
                FROM rooms
               WHERE location_id = ?
            ORDER BY room_id
               LIMIT ? OFFSET ?
            """;
    @Language("SQL")
    private static final String COUNT_BY_TYPE =
            "SELECT COUNT(*) FROM rooms WHERE type_id = ?";
    @Language("SQL")
    private static final String SELECT_PAGE_BY_TYPE = """
              SELECT room_id, location_id, type_id, name, capacity, description
                FROM rooms
               WHERE type_id = ?
            ORDER BY room_id
               LIMIT ? OFFSET ?
            """;
    private final RoomDao roomDao;
    private final CustomJdbcTemplate jdbc;

    public RoomRepository(RoomDao roomDao, CustomJdbcTemplate jdbc) {
        this.roomDao = roomDao;
        this.jdbc = jdbc;
    }

    public Optional<Room> findById(Long id) {
        try {
            return Optional.ofNullable(roomDao.findById(id));
        } catch (RuntimeException e) {
            log.error("findById({}) failed", id, e);
            return Optional.empty();
        }
    }

    public boolean existsById(Long id) {
        try {
            return roomDao.findById(id) != null;
        } catch (RuntimeException e) {
            log.error("existsById({}) failed", id, e);
            return false;
        }
    }

    public long count() {
        try {
            return roomDao.findAll().size();
        } catch (RuntimeException e) {
            log.error("count() failed", e);
            return 0L;
        }
    }

    public Page<Room> findAll(Pageable pg) {
        try {
            return QueryContainsMatcher.pageList(roomDao.findAll(), pg);
        } catch (RuntimeException e) {
            log.error("findAll(Pageable) failed", e);
            return QueryContainsMatcher.pageList(Collections.emptyList(), pg);
        }
    }

    public Room save(Room r) {
        try {
            if (r.getRoomId() == null) {
                roomDao.save(r);
            } else {
                roomDao.update(r);
            }
            return r;
        } catch (RuntimeException e) {
            log.error("save({}) failed", r, e);
            throw new RuntimeException("Could not save Room", e);
        }
    }

    public void deleteById(Long id) {
        try {
            Room existing = roomDao.findById(id);
            if (existing != null) {
                roomDao.delete(existing);
            }
        } catch (RuntimeException e) {
            log.error("deleteById({}) failed", id, e);
            throw new RuntimeException("Could not delete Room", e);
        }
    }

    public Optional<Room> findByName(String name) {
        try {
            return Optional.of(jdbc.queryForObject(
                    SELECT_BY_NAME, ROW_MAPPER, name
            ));
        } catch (RuntimeException e) {
            log.error("findByName({}) failed", name, e);
            return Optional.empty();
        }
    }

    public Page<Room> findByNameContaining(String kw, Pageable pg) {
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

    public Page<Room> findByLocationId(Long locationId, Pageable pg) {
        try {
            Long total = jdbc.queryForObject(
                    COUNT_BY_LOCATION, LONG_MAPPER, locationId
            );
            List<Room> list = jdbc.query(
                    SELECT_PAGE_BY_LOCATION, ROW_MAPPER,
                    locationId, pg.getPageSize(), pg.getOffset()
            );
            return new PageImpl<>(list, pg, total != null ? total : 0L);
        } catch (RuntimeException e) {
            log.error("findByLocationId({}, {}) failed", locationId, pg, e);
            return new PageImpl<>(Collections.emptyList(), pg, 0);
        }
    }

    public Page<Room> findByTypeId(Integer typeId, Pageable pg) {
        try {
            Long total = jdbc.queryForObject(
                    COUNT_BY_TYPE, LONG_MAPPER, typeId
            );
            List<Room> list = jdbc.query(
                    SELECT_PAGE_BY_TYPE, ROW_MAPPER,
                    typeId, pg.getPageSize(), pg.getOffset()
            );
            return new PageImpl<>(list, pg, total != null ? total : 0L);
        } catch (RuntimeException e) {
            log.error("findByTypeId({}, {}) failed", typeId, pg, e);
            return new PageImpl<>(Collections.emptyList(), pg, 0);
        }
    }
}
