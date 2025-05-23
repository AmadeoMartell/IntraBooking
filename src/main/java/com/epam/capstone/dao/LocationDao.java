package com.epam.capstone.dao;

import com.epam.capstone.mapper.row.LocationRowMapper;
import com.epam.capstone.model.Location;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class LocationDao implements CrudDao<Location, Long> {

    private static final String FIND_BY_ID_SQL = "SELECT * FROM locations WHERE location_id = ?";
    private static final String FIND_ALL_SQL = "SELECT * FROM locations";
    private static final String INSERT_SQL = "INSERT INTO locations (name, address, description) VALUES (?, ?, ?)";
    private static final String UPDATE_SQL = "UPDATE locations SET name = ?, address = ?, description = ? WHERE location_id = ?";
    private static final String DELETE_SQL = "DELETE FROM locations WHERE location_id = ?";

    private final JdbcTemplate jdbcTemplate;
    private final LocationRowMapper rowMapper;

    @Autowired
    public LocationDao(JdbcTemplate jdbcTemplate) {
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
