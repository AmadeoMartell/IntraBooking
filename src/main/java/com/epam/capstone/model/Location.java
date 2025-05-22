package com.epam.capstone.model;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Builder
@ToString
public class Location {
    @EqualsAndHashCode.Include
    private Long locationId;

    @EqualsAndHashCode.Include
    private String name;

    private String address;
    private String description;
}


