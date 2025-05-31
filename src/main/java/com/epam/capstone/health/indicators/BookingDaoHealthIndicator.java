package com.epam.capstone.health.indicators;

import com.epam.capstone.health.HealthIndicator;
import com.epam.capstone.health.HealthStatus;
import com.epam.capstone.model.Booking;
import com.epam.capstone.repository.dao.BookingDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class BookingDaoHealthIndicator implements HealthIndicator {

    private final BookingDao bookingDao;

    @Autowired
    public BookingDaoHealthIndicator(BookingDao bookingDao) {
        this.bookingDao = bookingDao;
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
            return new HealthStatus(getName(), HealthStatus.Status.UP, details);
        } catch (Exception ex) {
            Map<String, Object> details = new HashMap<>();
            details.put("exception", ex.getClass().getSimpleName());
            details.put("message", ex.getMessage());
            return new HealthStatus(getName(), HealthStatus.Status.DOWN, details);
        }
    }
}

