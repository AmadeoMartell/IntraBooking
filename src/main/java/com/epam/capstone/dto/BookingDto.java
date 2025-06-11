package com.epam.capstone.dto;

import java.time.LocalDateTime;

/**
 * Data transfer object for a booking record.
 *
 * @param bookingId  unique identifier of the booking
 * @param userId     identifier of the user who made the booking
 * @param roomId     identifier of the booked room
 * @param statusId   identifier of the booking status
 * @param startTime  start time of the booking
 * @param endTime    end time of the booking
 * @param purpose    purpose or description of the booking
 * @param createdAt  timestamp when the booking was created
 * @param updatedAt  timestamp when the booking was last updated
 */
public record BookingDto(
        Long bookingId,
        Long userId,
        Long roomId,
        Short statusId,
        LocalDateTime startTime,
        LocalDateTime endTime,
        String purpose,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}