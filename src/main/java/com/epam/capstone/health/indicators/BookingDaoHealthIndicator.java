package com.epam.capstone.health.indicators;

import com.epam.capstone.health.HealthIndicator;
import com.epam.capstone.health.HealthStatus;
import com.epam.capstone.model.Booking;
import com.epam.capstone.repository.dao.BookingDao;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Health indicator that verifies the Booking DAO can successfully load booking records.
 */
@Component
@Slf4j
public class BookingDaoHealthIndicator implements HealthIndicator {

    private final BookingDao bookingDao;

    /**
     * Create a new BookingDaoHealthIndicator.
     *
     * @param bookingDao the DAO used to retrieve bookings
     */
    @Autowired
    public BookingDaoHealthIndicator(BookingDao bookingDao) {
        this.bookingDao = bookingDao;
        log.debug("Initialized BookingDaoHealthIndicator");
    }

    @Override
    public String getName() {
        return "bookingRepository";
    }

    @Override
    public HealthStatus checkHealth() {
        try {
            List<Booking> bookings = bookingDao.findAll();
            Map<String, Object> details = new HashMap<>();
            details.put("count", bookings.size());
            log.debug("BookingDao returned {} records", bookings.size());
            return new HealthStatus(getName(), HealthStatus.Status.UP, details);
        } catch (Exception ex) {
            log.error("Exception during BookingDao health check", ex);
            Map<String, Object> details = new HashMap<>();
            details.put("exception", ex.getClass().getSimpleName());
            details.put("message", ex.getMessage());
            return new HealthStatus(getName(), HealthStatus.Status.DOWN, details);
        }
    }
}

