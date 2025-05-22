package com.epam.capstone.model;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString
@Builder
public class RoomType {
    @EqualsAndHashCode.Include
    private Integer typeId;

    @EqualsAndHashCode.Include
    private String name;

    private Integer capacity;
    private String description;
}


