package com.epam.capstone.model;

import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
@Builder
public class RoomType {
    @EqualsAndHashCode.Include
    @ToString.Include
    private int typeId;

    @EqualsAndHashCode.Include
    @ToString.Include
    private String name;

    private int capacity;
    private String description;

    private List<Room> rooms;
}


