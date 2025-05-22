package com.epam.capstone.model;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString
@Builder
public class Room {
    @EqualsAndHashCode.Include
    private Long roomId;

    private Long locationId;
    private Integer typeId;

    @EqualsAndHashCode.Include
    private String name;

    private Integer capacity;
    private String description;
}


