package com.epam.capstone.repository.dao.rowmapper;

import com.epam.capstone.model.Location;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class LocationRowMapper implements RowMapper<Location> {
    @Override
    public Location mapRow(ResultSet rs, int rowNum) throws SQLException {
        return Location.builder()
                .locationId(rs.getLong("location_id"))
                .name(rs.getString("name"))
                .address(rs.getString("address"))
                .description(rs.getString("description"))
                .build();
    }
}
