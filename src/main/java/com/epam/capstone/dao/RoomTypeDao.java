package com.epam.capstone.dao;

import com.epam.capstone.mapper.row.RoomTypeRowMapper;
import com.epam.capstone.model.RoomType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class RoomTypeDao implements CrudDao<RoomType, Integer> {

    private static final String FIND_BY_ID_SQL = "SELECT * FROM room_types WHERE type_id = ?";
    private static final String FIND_ALL_SQL = "SELECT * FROM room_types";
    private static final String INSERT_SQL = "INSERT INTO room_types (name, capacity, description) VALUES (?, ?, ?)";
    private static final String UPDATE_SQL = "UPDATE room_types SET name = ?, capacity = ?, description = ? WHERE type_id = ?";
    private static final String DELETE_SQL = "DELETE FROM room_types WHERE type_id = ?";

    private final JdbcTemplate jdbcTemplate;
    private final RoomTypeRowMapper rowMapper;

    @Autowired
    public RoomTypeDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.rowMapper = new RoomTypeRowMapper();
    }

    @Override
    public RoomType findById(Integer id) {
        return jdbcTemplate.queryForObject(FIND_BY_ID_SQL, rowMapper, id);
    }

    @Override
    public List<RoomType> findAll() {
        return jdbcTemplate.query(FIND_ALL_SQL, rowMapper);
    }

    @Override
    public void save(RoomType roomType) {
        jdbcTemplate.update(INSERT_SQL, roomType.getName(), roomType.getCapacity(), roomType.getDescription());
    }

    @Override
    public void update(RoomType roomType) {
        jdbcTemplate.update(UPDATE_SQL, roomType.getName(), roomType.getCapacity(), roomType.getDescription(), roomType.getTypeId());
    }

    @Override
    public void delete(RoomType roomType) {
        jdbcTemplate.update(DELETE_SQL, roomType.getTypeId());
    }
}
