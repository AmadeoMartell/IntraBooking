package com.epam.capstone.model;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString
@Builder
public class Role {
    @EqualsAndHashCode.Include
    private Long roleId;

    @EqualsAndHashCode.Include
    private String name;

    private String description;
}


