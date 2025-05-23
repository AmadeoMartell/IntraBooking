package com.epam.capstone.model;

import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString
@Builder
public class User {
    @EqualsAndHashCode.Include
    private Long userId;

    private Long roleID;

    @EqualsAndHashCode.Include
    private String username;

    private String passwordHash;
    private String fullName;

    @EqualsAndHashCode.Include
    private String email;

    private String phone;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}


