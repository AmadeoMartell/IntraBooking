package com.epam.capstone.repository;

import com.epam.capstone.repository.dao.LocationDao;
import com.epam.capstone.repository.dao.rowmapper.LocationRowMapper;
import com.epam.capstone.model.Location;
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
public class LocationRepository {

    private static final LocationRowMapper ROW_MAPPER = new LocationRowMapper();
    private static final SingleColumnRowMapper<Long> LONG_MAPPER =
            SingleColumnRowMapper.newInstance(Long.class);
    @Language("SQL")
    private static final String SELECT_BY_NAME = """
            SELECT location_id, name, address, description
              FROM locations
             WHERE name = ?
            """;
    @Language("SQL")
    private static final String COUNT_BY_NAME_CONTAINS =
            "SELECT COUNT(*) FROM locations WHERE name ILIKE ?";

    @Language("SQL")
    private static final String SELECT_PAGE_BY_NAME_CONTAINS = """
              SELECT location_id, name, address, description
                FROM locations
               WHERE name ILIKE ?
            ORDER BY location_id
               LIMIT ? OFFSET ?
            """;
    @Language("SQL")
    private static final String COUNT_BY_ADDRESS_CONTAINS =
            "SELECT COUNT(*) FROM locations WHERE address ILIKE ?";
    @Language("SQL")
    private static final String SELECT_PAGE_BY_ADDRESS_CONTAINS = """
              SELECT location_id, name, address, description
                FROM locations
               WHERE address ILIKE ?
            ORDER BY location_id
               LIMIT ? OFFSET ?
            """;
    @Language("SQL")
    private static final String COUNT_BY_DESCRIPTION_CONTAINS =
            "SELECT COUNT(*) FROM locations WHERE description ILIKE ?";
    @Language("SQL")
    private static final String SELECT_PAGE_BY_DESCRIPTION_CONTAINS = """
              SELECT location_id, name, address, description
                FROM locations
               WHERE description ILIKE ?
            ORDER BY location_id
               LIMIT ? OFFSET ?
            """;
    private final LocationDao locationDao;
    private final CustomJdbcTemplate jdbc;

    public LocationRepository(LocationDao locationDao, CustomJdbcTemplate jdbc) {
        this.locationDao = locationDao;
        this.jdbc = jdbc;
    }

    public Optional<Location> findById(Long id) {
        try {
            return Optional.ofNullable(locationDao.findById(id));
        } catch (RuntimeException e) {
            log.error("findById({}) failed", id, e);
            return Optional.empty();
        }
    }

    public boolean existsById(Long id) {
        try {
            return locationDao.findById(id) != null;
        } catch (RuntimeException e) {
            log.error("existsById({}) failed", id, e);
            return false;
        }
    }

    public long count() {
        try {
            return locationDao.findAll().size();
        } catch (RuntimeException e) {
            log.error("count() failed", e);
            return 0L;
        }
    }

    public Page<Location> findAll(Pageable pg) {
        try {
            return QueryContainsMatcher.pageList(locationDao.findAll(), pg);
        } catch (RuntimeException e) {
            log.error("findAll(Pageable) failed", e);
            return QueryContainsMatcher.pageList(Collections.emptyList(), pg);
        }
    }

    public Location save(Location loc) {
        try {
            if (loc.getLocationId() == null) {
                locationDao.save(loc);
            } else {
                locationDao.update(loc);
            }
            return loc;
        } catch (RuntimeException e) {
            log.error("save({}) failed", loc, e);
            throw new RuntimeException("Could not save Location", e);
        }
    }

    public void deleteById(Long id) {
        try {
            Location existing = locationDao.findById(id);
            if (existing != null) {
                locationDao.delete(existing);
            }
        } catch (RuntimeException e) {
            log.error("deleteById({}) failed", id, e);
            throw new RuntimeException("Could not delete Location", e);
        }
    }

    public Optional<Location> findByName(String name) {
        try {
            return Optional.of(jdbc.queryForObject(
                    SELECT_BY_NAME, ROW_MAPPER, name
            ));
        } catch (RuntimeException e) {
            log.error("findByName({}) failed", name, e);
            return Optional.empty();
        }
    }

    public Page<Location> findByNameContaining(String kw, Pageable pg) {
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

    public Page<Location> findByAddressContaining(String kw, Pageable pg) {
        return QueryContainsMatcher.findByQueryContaining(
                jdbc,
                COUNT_BY_ADDRESS_CONTAINS,
                SELECT_PAGE_BY_ADDRESS_CONTAINS,
                LONG_MAPPER,
                ROW_MAPPER,
                kw,
                pg
        );
    }

    public Page<Location> findByDescriptionContaining(String kw, Pageable pg) {
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
