package com.epam.capstone.repository.dao;

import com.epam.capstone.util.database.CustomJdbcTemplate;
import com.epam.capstone.repository.dao.rowmapper.RoomTypeRowMapper;
import com.epam.capstone.model.RoomType;
import org.intellij.lang.annotations.Language;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class RoomTypeDao implements CrudDao<RoomType, Integer> {

    @Language("SQL")
    private static final String FIND_BY_ID_SQL = "SELECT * FROM room_types WHERE type_id = ?";
    @Language("SQL")
    private static final String FIND_ALL_SQL = "SELECT * FROM room_types";
    @Language("SQL")
    private static final String INSERT_SQL = "INSERT INTO room_types (name, capacity, description) VALUES (?, ?, ?)";
    @Language("SQL")
    private static final String UPDATE_SQL = "UPDATE room_types SET name = ?, capacity = ?, description = ? WHERE type_id = ?";
    @Language("SQL")
    private static final String DELETE_SQL = "DELETE FROM room_types WHERE type_id = ?";

    private final CustomJdbcTemplate jdbcTemplate;
    private final RoomTypeRowMapper rowMapper;

    @Autowired
    public RoomTypeDao(CustomJdbcTemplate jdbcTemplate) {
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
