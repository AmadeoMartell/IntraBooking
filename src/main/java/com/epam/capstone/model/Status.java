package com.epam.capstone.model;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString
@Builder
public class Status {
    @EqualsAndHashCode.Include
    private Short statusId;

    @EqualsAndHashCode.Include
    private String name;
}


