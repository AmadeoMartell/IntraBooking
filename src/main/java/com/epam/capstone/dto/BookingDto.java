package com.epam.capstone.dto;

import java.time.LocalDateTime;

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