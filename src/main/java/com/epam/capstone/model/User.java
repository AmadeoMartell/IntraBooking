package com.epam.capstone.model;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
@Builder
public class User {
    @EqualsAndHashCode.Include
    @ToString.Include
    private int userId;

    private Role role;

    @EqualsAndHashCode.Include
    @ToString.Include
    private String username;

    private String passwordHash;
    private String fullName;

    @EqualsAndHashCode.Include
    @ToString.Include
    private String email;

    private String phone;
    private LocalDateTime createdAt;

    private List<Booking> bookings;
}


