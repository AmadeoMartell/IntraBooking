package com.epam.capstone.model;

import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
@Builder
public class Room {
    @EqualsAndHashCode.Include
    @ToString.Include
    private int roomId;

    private Location location;
    private RoomType type;

    @EqualsAndHashCode.Include
    @ToString.Include
    private String name;

    private int capacity;
    private String description;

    private List<Booking> bookings;
}


