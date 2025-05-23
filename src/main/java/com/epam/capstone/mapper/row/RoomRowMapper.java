package com.epam.capstone.mapper.row;

import com.epam.capstone.model.Room;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class RoomRowMapper implements RowMapper<Room> {
    @Override
    public Room mapRow(ResultSet rs, int rowNum) throws SQLException {
        return Room.builder()
                .roomId(rs.getLong("room_id"))
                .locationId(rs.getLong("location_id"))
                .typeId(rs.getInt("type_id"))
                .name(rs.getString("name"))
                .capacity(rs.getInt("capacity"))
                .description(rs.getString("description"))
                .build();
    }
}
