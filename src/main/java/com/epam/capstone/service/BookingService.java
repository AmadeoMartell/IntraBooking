package com.epam.capstone.service;

import com.epam.capstone.dto.BookingDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service interface for managing bookings.
 */
public interface BookingService {

    /**
     * Create a new booking.
     *
     * @param bookingDto DTO containing booking data
     * @return DTO of the created booking
     */
    BookingDto createBooking(BookingDto bookingDto);

    /**
     * Retrieve a booking by its ID.
     *
     * @param bookingId ID of the booking to retrieve
     * @return DTO of the found booking
     */
    BookingDto getBookingById(Long bookingId);

    /**
     * Retrieve bookings for a specific user by their ID with pagination.
     *
     * @param userId   ID of the user whose bookings to retrieve
     * @param pageable pagination information
     * @return page of booking DTOs for the given user
     */
    Page<BookingDto> getBookingsByUser(Long userId, Pageable pageable);

    /**
     * Update an existing booking.
     *
     * @param bookingId  ID of the booking to update
     * @param bookingDto DTO containing new values for the booking
     * @return DTO of the updated booking
     */
    BookingDto updateBooking(Long bookingId, BookingDto bookingDto);

    /**
     * Delete a booking by its ID.
     *
     * @param bookingId ID of the booking to delete
     */
    void deleteBooking(Long bookingId);

    /**
     * Create a new booking for a user identified by username.
     *
     * @param username   username of the user for whom to create the booking
     * @param bookingDto DTO containing booking data
     * @return DTO of the created booking
     */
    BookingDto createBookingForUser(String username, BookingDto bookingDto);

    /**
     * Retrieve bookings for a user by their username with pagination.
     *
     * @param username username of the user whose bookings to retrieve
     * @param pageable pagination information
     * @return page of booking DTOs for the given username
     */
    Page<BookingDto> getBookingsByUsername(String username, Pageable pageable);

    /**
     * Delete a booking for a specific user identified by username.
     *
     * @param username  username of the user
     * @param bookingId ID of the booking to delete for that user
     */
    void deleteBookingForUser(String username, Long bookingId);
}
