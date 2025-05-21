package com.epam.capstone.model;

import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
@Builder
public class Booking {
    @EqualsAndHashCode.Include
    @ToString.Include
    private long bookingId;

    private User user;
    private Room room;
    private Status status;

    @EqualsAndHashCode.Include
    @ToString.Include
    private LocalDateTime startTime;

    @EqualsAndHashCode.Include
    @ToString.Include
    private LocalDateTime endTime;

    private String purpose;

    @EqualsAndHashCode.Include
    @ToString.Include
    private LocalDateTime createdAt;
}


