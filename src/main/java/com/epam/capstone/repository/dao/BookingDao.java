package com.epam.capstone.repository.dao;

import com.epam.capstone.util.database.CustomJdbcTemplate;
import com.epam.capstone.repository.dao.rowmapper.BookingRowMapper;
import com.epam.capstone.model.Booking;
import org.intellij.lang.annotations.Language;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public class BookingDao implements CrudDao<Booking, Long> {

    @Language("SQL")
    private static final String FIND_BY_ID_SQL = "SELECT * FROM bookings WHERE booking_id = ?";
    @Language("SQL")
    private static final String FIND_ALL_SQL = "SELECT * FROM bookings";
    @Language("SQL")
    private static final String INSERT_SQL =
            "INSERT INTO bookings (user_id, room_id, status_id, start_time, end_time, purpose) VALUES (?, ?, ?, ?, ?, ?)";
    @Language("SQL")
    private static final String UPDATE_SQL =
            "UPDATE bookings SET user_id = ?, room_id = ?, status_id = ?, start_time = ?, end_time = ?, purpose = ?, updated_at = ? WHERE booking_id = ?";
    @Language("SQL")
    private static final String DELETE_SQL = "DELETE FROM bookings WHERE booking_id = ?";

    private final CustomJdbcTemplate jdbcTemplate;
    private final BookingRowMapper rowMapper;

    @Autowired
    public BookingDao(CustomJdbcTemplate jdbcTemplate) {
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
                booking.getBookingId(),
                LocalDateTime.now());
    }

    @Override
    public void delete(Booking booking) {
        jdbcTemplate.update(DELETE_SQL, booking.getBookingId());
    }
}
