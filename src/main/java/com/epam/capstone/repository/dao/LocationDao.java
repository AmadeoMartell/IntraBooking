package com.epam.capstone.repository.dao;

import com.epam.capstone.util.database.CustomJdbcTemplate;
import com.epam.capstone.repository.dao.rowmapper.LocationRowMapper;
import com.epam.capstone.model.Location;
import org.intellij.lang.annotations.Language;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class LocationDao implements CrudDao<Location, Long> {

    @Language("SQL")
    private static final String FIND_BY_ID_SQL = "SELECT * FROM locations WHERE location_id = ?";
    @Language("SQL")
    private static final String FIND_ALL_SQL = "SELECT * FROM locations";
    @Language("SQL")
    private static final String INSERT_SQL = "INSERT INTO locations (name, address, description) VALUES (?, ?, ?)";
    @Language("SQL")
    private static final String UPDATE_SQL = "UPDATE locations SET name = ?, address = ?, description = ? WHERE location_id = ?";
    @Language("SQL")
    private static final String DELETE_SQL = "DELETE FROM locations WHERE location_id = ?";

    private final CustomJdbcTemplate jdbcTemplate;
    private final LocationRowMapper rowMapper;

    @Autowired
    public LocationDao(CustomJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.rowMapper = new LocationRowMapper();
    }

    @Override
    public Location findById(Long id) {
        return jdbcTemplate.queryForObject(FIND_BY_ID_SQL, rowMapper, id);
    }

    @Override
    public List<Location> findAll() {
        return jdbcTemplate.query(FIND_ALL_SQL, rowMapper);
    }

    @Override
    public void save(Location location) {
        jdbcTemplate.update(INSERT_SQL,
                location.getName(),
                location.getAddress(),
                location.getDescription());
    }

    @Override
    public void update(Location location) {
        jdbcTemplate.update(UPDATE_SQL,
                location.getName(),
                location.getAddress(),
                location.getDescription(),
                location.getLocationId());
    }

    @Override
    public void delete(Location location) {
        jdbcTemplate.update(DELETE_SQL, location.getLocationId());
    }
}
