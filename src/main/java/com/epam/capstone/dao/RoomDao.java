package com.epam.capstone.dao;

import com.epam.capstone.dao.rowmapper.RoomRowMapper;
import com.epam.capstone.model.Room;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class RoomDao implements CrudDao<Room, Long> {
    private static final String FIND_BY_ID_SQL = "SELECT * FROM rooms WHERE room_id = ?";
    private static final String FIND_ALL_SQL = "SELECT * FROM rooms";
    private static final String INSERT_SQL = "INSERT INTO rooms " +
            "(location_id, type_id, name, capacity, description) VALUES (?, ?, ?, ?, ?)";
    private static final String UPDATE_SQL = "UPDATE rooms SET " +
            "location_id = ?, type_id = ?, name = ?, capacity = ?, description = ? WHERE room_id = ?";
    private static final String DELETE_SQL = "DELETE FROM rooms WHERE room_id = ?";

    private final JdbcTemplate jdbcTemplate;
    private final RoomRowMapper rowMapper;

    @Autowired
    public RoomDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.rowMapper = new RoomRowMapper();
    }

    @Override
    public Room findById(Long id) {
        return jdbcTemplate.queryForObject(FIND_BY_ID_SQL, rowMapper, id);
    }

    @Override
    public List<Room> findAll() {
        return jdbcTemplate.query(FIND_ALL_SQL, rowMapper);
    }

    @Override
    public void save(Room room) {
        jdbcTemplate.update(INSERT_SQL,
                room.getLocationId(),
                room.getTypeId(),
                room.getName(),
                room.getCapacity(),
                room.getDescription());
    }

    @Override
    public void update(Room room) {
        jdbcTemplate.update(UPDATE_SQL,
                room.getLocationId(),
                room.getTypeId(),
                room.getName(),
                room.getCapacity(),
                room.getDescription(),
                room.getRoomId());
    }

    @Override
    public void delete(Room room) {
        jdbcTemplate.update(DELETE_SQL, room.getRoomId());
    }
}
