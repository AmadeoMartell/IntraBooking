package com.epam.capstone.service;

import com.epam.capstone.dto.BookingDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BookingService {

    BookingDto createBooking(BookingDto bookingDto);

    BookingDto getBookingById(Long bookingId);

    Page<BookingDto> getBookingsByUser(Long userId, Pageable pageable);

    BookingDto updateBooking(Long bookingId, BookingDto bookingDto);

    void deleteBooking(Long bookingId);

    BookingDto createBookingForUser(String username, BookingDto bookingDto);

    Page<BookingDto> getBookingsByUsername(String username, Pageable pageable);

    void deleteBookingForUser(String username, Long bookingId);
}
