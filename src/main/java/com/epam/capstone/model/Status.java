package com.epam.capstone.model;

import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
@Builder
public class Status {
    @EqualsAndHashCode.Include
    @ToString.Include
    private short statusId;

    @EqualsAndHashCode.Include
    @ToString.Include
    private String name;

    private List<Booking> bookings;
}


