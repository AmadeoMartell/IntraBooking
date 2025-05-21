package com.epam.capstone.model;

import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
@Builder
public class Location {
    @EqualsAndHashCode.Include
    @ToString.Include
    private int locationId;

    @EqualsAndHashCode.Include
    @ToString.Include
    private String name;

    private String address;
    private String description;

    private List<Room> rooms;
}


