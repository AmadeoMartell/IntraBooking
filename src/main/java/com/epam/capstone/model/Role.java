package com.epam.capstone.model;

import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
@Builder
public class Role {
    @EqualsAndHashCode.Include
    @ToString.Include
    private int roleId;

    @EqualsAndHashCode.Include
    @ToString.Include
    private String name;

    private String description;

    private List<User> users;
}


