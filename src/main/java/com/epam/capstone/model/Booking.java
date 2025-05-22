package com.epam.capstone.model;

import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString
@Builder
public class Booking {
    @EqualsAndHashCode.Include
    private Long bookingId;

    private Long userId;
    private Long roomId;
    private Short statusId;

    @EqualsAndHashCode.Include
    private LocalDateTime startTime;

    @EqualsAndHashCode.Include
    private LocalDateTime endTime;

    private String purpose;

    @EqualsAndHashCode.Include
    private LocalDateTime createdAt;
}


