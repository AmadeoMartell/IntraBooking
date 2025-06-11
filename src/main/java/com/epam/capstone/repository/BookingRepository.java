package com.epam.capstone.repository;

import com.epam.capstone.model.Booking;
import com.epam.capstone.repository.dao.BookingDao;
import com.epam.capstone.repository.dao.rowmapper.BookingRowMapper;
import com.epam.capstone.repository.helper.QueryContainsMatcher;
import com.epam.capstone.util.database.CustomJdbcTemplate;
import lombok.extern.slf4j.Slf4j;
import org.intellij.lang.annotations.Language;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.jdbc.core.SingleColumnRowMapper;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Slf4j
@Repository
public class BookingRepository {

    private static final BookingRowMapper ROW_MAPPER = new BookingRowMapper();
    private static final SingleColumnRowMapper<Long> LONG_MAPPER =
            SingleColumnRowMapper.newInstance(Long.class);
    @Language("SQL")
    private static final String COUNT_BY_USER =
            "SELECT COUNT(*) FROM bookings WHERE user_id = ?";
    @Language("SQL")
    private static final String SELECT_PAGE_BY_USER = """
              SELECT booking_id, user_id, room_id, status_id,
                     start_time, end_time, purpose,
                     created_at, updated_at
                FROM bookings
               WHERE user_id = ?
              ORDER BY start_time
               LIMIT ? OFFSET ?
            """;
    @Language("SQL")
    private static final String SELECT_PAGE_BY_USER_DESC = """
      SELECT booking_id, user_id, room_id, status_id,
             start_time, end_time, purpose,
             created_at, updated_at
        FROM bookings
       WHERE user_id = ?
    ORDER BY start_time DESC
       LIMIT ? OFFSET ?
    """;
    @Language("SQL")
    private static final String COUNT_BY_ROOM =
            "SELECT COUNT(*) FROM bookings WHERE room_id = ?";
    @Language("SQL")
    private static final String SELECT_PAGE_BY_ROOM = """
              SELECT booking_id, user_id, room_id, status_id,
                     start_time, end_time, purpose,
                     created_at, updated_at
                FROM bookings
               WHERE room_id = ?
            ORDER BY booking_id
               LIMIT ? OFFSET ?
            """;
    @Language("SQL")
    private static final String COUNT_BY_STATUS =
            "SELECT COUNT(*) FROM bookings WHERE status_id = ?";
    @Language("SQL")
    private static final String SELECT_PAGE_BY_STATUS = """
              SELECT booking_id, user_id, room_id, status_id,
                     start_time, end_time, purpose,
                     created_at, updated_at
                FROM bookings
               WHERE status_id = ?
            ORDER BY start_time
               LIMIT ? OFFSET ?
            """;
    @Language("SQL")
    private static final String SELECT_PAGE_BY_STATUS_DESC = """
              SELECT booking_id, user_id, room_id, status_id,
                     start_time, end_time, purpose,
                     created_at, updated_at
                FROM bookings
               WHERE status_id = ?
            ORDER BY start_time DESC
               LIMIT ? OFFSET ?
            """;
    @Language("SQL")
    private static final String COUNT_BY_START_RANGE =
            "SELECT COUNT(*) FROM bookings WHERE start_time BETWEEN ? AND ?";
    @Language("SQL")
    private static final String SELECT_PAGE_BY_START_RANGE = """
              SELECT booking_id, user_id, room_id, status_id,
                     start_time, end_time, purpose,
                     created_at, updated_at
                FROM bookings
               WHERE start_time BETWEEN ? AND ?
            ORDER BY booking_id
               LIMIT ? OFFSET ?
            """;
    @Language("SQL")
    private static final String COUNT_BY_PURPOSE =
            "SELECT COUNT(*) FROM bookings WHERE purpose ILIKE ?";
    @Language("SQL")
    private static final String SELECT_PAGE_BY_PURPOSE = """
              SELECT booking_id, user_id, room_id, status_id,
                     start_time, end_time, purpose,
                     created_at, updated_at
                FROM bookings
               WHERE purpose ILIKE ?
            ORDER BY booking_id
               LIMIT ? OFFSET ?
            """;
    @Language("SQL")
    private static final String COUNT_BY_USER_AND_STATUS =
            "SELECT COUNT(*) FROM bookings WHERE user_id = ? AND status_id = ?";
    @Language("SQL")
    private static final String SELECT_PAGE_BY_USER_AND_STATUS_ASC = """
              SELECT booking_id, user_id, room_id, status_id,
                     start_time, end_time, purpose,
                     created_at, updated_at
                FROM bookings
               WHERE user_id = ? AND status_id = ?
            ORDER BY start_time ASC
               LIMIT ? OFFSET ?
            """;
    @Language("SQL")
    private static final String SELECT_PAGE_BY_USER_AND_STATUS_DESC = """
              SELECT booking_id, user_id, room_id, status_id,
                     start_time, end_time, purpose,
                     created_at, updated_at
                FROM bookings
               WHERE user_id = ? AND status_id = ?
            ORDER BY start_time DESC
               LIMIT ? OFFSET ?
            """;
    @Language("SQL")
    private static final String COUNT_OVERLAPPING =
            "SELECT COUNT(*) " +
                    "  FROM bookings " +
                    " WHERE room_id = ? " +
                    "   AND start_time < ? " +
                    "   AND end_time   > ?";

    private final BookingDao bookingDao;
    private final CustomJdbcTemplate jdbc;

    public BookingRepository(BookingDao bookingDao,
                             CustomJdbcTemplate jdbc) {
        this.bookingDao = bookingDao;
        this.jdbc = jdbc;
    }

    public Optional<Booking> findById(Long id) {
        try {
            return Optional.ofNullable(bookingDao.findById(id));
        } catch (RuntimeException e) {
            log.error("findById({}) failed", id, e);
            return Optional.empty();
        }
    }

    public boolean existsById(Long id) {
        try {
            return bookingDao.findById(id) != null;
        } catch (RuntimeException e) {
            log.error("existsById({}) failed", id, e);
            return false;
        }
    }

    public long count() {
        try {
            return bookingDao.findAll().size();
        } catch (RuntimeException e) {
            log.error("count() failed", e);
            return 0L;
        }
    }

    public Page<Booking> findAll(Pageable pg) {
        try {
            return QueryContainsMatcher.pageList(bookingDao.findAll(), pg);
        } catch (RuntimeException e) {
            log.error("findAll(Pageable) failed", e);
            return QueryContainsMatcher.pageList(Collections.emptyList(), pg);
        }
    }

    public Booking save(Booking b) {
        try {
            if (b.getBookingId() == null) {
                bookingDao.save(b);
            } else {
                bookingDao.update(b);
            }
            return b;
        } catch (RuntimeException e) {
            log.error("save({}) failed", b, e);
            throw new RuntimeException("Could not save Booking", e);
        }
    }

    public void deleteById(Long id) {
        try {
            Booking existing = bookingDao.findById(id);
            if (existing != null) {
                bookingDao.delete(existing);
            }
        } catch (RuntimeException e) {
            log.error("deleteById({}) failed", id, e);
            throw new RuntimeException("Could not delete Booking", e);
        }
    }

    public Page<Booking> findAllByUserId(Long userId, Pageable pg) {
        Sort.Order order = pg.getSort().getOrderFor("startTime");
        boolean descending = (order != null && order.getDirection().isDescending());
        @Language("SQL") String sql = descending ? SELECT_PAGE_BY_USER_DESC : SELECT_PAGE_BY_USER;

        Long total = jdbc.queryForObject(COUNT_BY_USER, LONG_MAPPER, userId);
        List<Booking> list = jdbc.query(
                sql, ROW_MAPPER,
                userId, pg.getPageSize(), pg.getOffset()
        );
        return new PageImpl<>(list, pg, total != null ? total : 0L);
    }

    public Page<Booking> findAllByRoomId(Long roomId, Pageable pg) {
        try {
            Long total = jdbc.queryForObject(
                    COUNT_BY_ROOM, LONG_MAPPER, roomId
            );
            List<Booking> list = jdbc.query(
                    SELECT_PAGE_BY_ROOM, ROW_MAPPER,
                    roomId, pg.getPageSize(), pg.getOffset()
            );
            return new PageImpl<>(list, pg, total != null ? total : 0L);
        } catch (RuntimeException e) {
            log.error("findAllByRoomId({}, {}) failed", roomId, pg, e);
            return new PageImpl<>(Collections.emptyList(), pg, 0);
        }
    }

    public Page<Booking> findAllByStatusId(Short statusId, Pageable pg) {
        Sort.Order order = pg.getSort().getOrderFor("startTime");
        boolean descending = (order != null && order.getDirection().isDescending());
        @Language("SQL") String sql = descending ? SELECT_PAGE_BY_STATUS_DESC : SELECT_PAGE_BY_STATUS;
        try {
            Long total = jdbc.queryForObject(
                    COUNT_BY_STATUS, LONG_MAPPER, statusId
            );
            List<Booking> list = jdbc.query(
                    sql, ROW_MAPPER,
                    statusId, pg.getPageSize(), pg.getOffset()
            );
            return new PageImpl<>(list, pg, total != null ? total : 0L);
        } catch (RuntimeException e) {
            log.error("findAllByStatusId({}, {}) failed", statusId, pg, e);
            return new PageImpl<>(Collections.emptyList(), pg, 0);
        }
    }

    public Page<Booking> findAllByUserIdAndStatusId(
            Long userId, Short statusId, Pageable pg) {
        try {
            Sort.Order order = pg.getSort().getOrderFor("startTime");
            boolean descending = (order != null && order.getDirection().isDescending());

            @Language("SQL") String selectSql = descending
                    ? SELECT_PAGE_BY_USER_AND_STATUS_DESC
                    : SELECT_PAGE_BY_USER_AND_STATUS_ASC;

            Long total = jdbc.queryForObject(
                    COUNT_BY_USER_AND_STATUS, LONG_MAPPER, userId, statusId
            );
            List<Booking> list = jdbc.query(
                    selectSql, ROW_MAPPER,
                    userId, statusId, pg.getPageSize(), pg.getOffset()
            );
            return new PageImpl<>(list, pg, total != null ? total : 0L);
        } catch (RuntimeException e) {
            log.error("findAllByUserIdAndStatusId({}, {}, {}) failed", userId, statusId, pg, e);
            return new PageImpl<>(Collections.emptyList(), pg, 0);
        }
    }

    public Page<Booking> findByStartTimeBetween(
            LocalDateTime from, LocalDateTime to, Pageable pg) {
        try {
            Long total = jdbc.queryForObject(
                    COUNT_BY_START_RANGE, LONG_MAPPER,
                    Timestamp.valueOf(from), Timestamp.valueOf(to)
            );
            List<Booking> list = jdbc.query(
                    SELECT_PAGE_BY_START_RANGE, ROW_MAPPER,
                    Timestamp.valueOf(from), Timestamp.valueOf(to),
                    pg.getPageSize(), pg.getOffset()
            );
            return new PageImpl<>(list, pg, total != null ? total : 0L);
        } catch (RuntimeException e) {
            log.error("findByStartTimeBetween({}, {}, {}) failed", from, to, pg, e);
            return new PageImpl<>(Collections.emptyList(), pg, 0);
        }
    }

    public Page<Booking> findByPurposeContaining(String kw, Pageable pg) {
        return QueryContainsMatcher.findByQueryContaining(
                jdbc,
                COUNT_BY_PURPOSE,
                SELECT_PAGE_BY_PURPOSE,
                LONG_MAPPER,
                ROW_MAPPER,
                kw,
                pg
        );
    }

    public long countOverlappingBookings(
            Long roomId,
            LocalDateTime desiredStart,
            LocalDateTime desiredEnd
    ) {
        try {
            Timestamp endTs   = Timestamp.valueOf(desiredEnd);
            Timestamp startTs = Timestamp.valueOf(desiredStart);
            Long cnt = jdbc.queryForObject(
                    COUNT_OVERLAPPING,
                    LONG_MAPPER,
                    roomId, endTs, startTs
            );
            return (cnt != null ? cnt : 0L);
        } catch (RuntimeException ex) {
            log.error("countOverlappingBookings({}, {}, {}) failed",
                    roomId, desiredStart, desiredEnd, ex);
            throw ex;
        }
    }
}
