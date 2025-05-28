package com.epam.capstone.repository.dao.rowmapper;

import com.epam.capstone.model.RoomType;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class RoomTypeRowMapper implements RowMapper<RoomType> {
    @Override
    public RoomType mapRow(ResultSet rs, int rowNum) throws SQLException {
        return RoomType.builder()
                .typeId(rs.getInt("type_id"))
                .name(rs.getString("name"))
                .capacity(rs.getInt("capacity"))
                .description(rs.getString("description"))
                .build();
    }
}
