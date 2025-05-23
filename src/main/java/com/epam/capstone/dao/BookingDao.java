package com.epam.capstone.dao;

import com.epam.capstone.mapper.row.BookingRowMapper;
import com.epam.capstone.model.Booking;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class BookingDao implements CrudDao<Booking, Long> {

    private static final String FIND_BY_ID_SQL =
            "SELECT * FROM bookings WHERE booking_id = ?";
    private static final String FIND_ALL_SQL =
            "SELECT * FROM bookings";
    private static final String INSERT_SQL =
            "INSERT INTO bookings (user_id, room_id, status_id, start_time, end_time, purpose) VALUES (?, ?, ?, ?, ?, ?)";
    private static final String UPDATE_SQL =
            "UPDATE bookings SET user_id = ?, room_id = ?, status_id = ?, start_time = ?, end_time = ?, purpose = ? WHERE booking_id = ?";
    private static final String DELETE_SQL =
            "DELETE FROM bookings WHERE booking_id = ?";

    private final JdbcTemplate jdbcTemplate;
    private final BookingRowMapper rowMapper;

    @Autowired
    public BookingDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.rowMapper = new BookingRowMapper();
    }

    @Override
    public Booking findById(Long id) {
        return jdbcTemplate.queryForObject(FIND_BY_ID_SQL, rowMapper, id);
    }

    @Override
    public List<Booking> findAll() {
        return jdbcTemplate.query(FIND_ALL_SQL, rowMapper);
    }

    @Override
    public void save(Booking booking) {
        jdbcTemplate.update(INSERT_SQL,
                booking.getUserId(),
                booking.getRoomId(),
                booking.getStatusId(),
                booking.getStartTime(),
                booking.getEndTime(),
                booking.getPurpose());
    }

    @Override
    public void update(Booking booking) {
        jdbcTemplate.update(UPDATE_SQL,
                booking.getUserId(),
                booking.getRoomId(),
                booking.getStatusId(),
                booking.getStartTime(),
                booking.getEndTime(),
                booking.getPurpose(),
                booking.getBookingId());
    }

    @Override
    public void delete(Booking booking) {
        jdbcTemplate.update(DELETE_SQL, booking.getBookingId());
    }
}
