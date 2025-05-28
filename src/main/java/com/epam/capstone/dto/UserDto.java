package com.epam.capstone.dto;

import java.time.LocalDateTime;

public record UserDto(
        Long userId,
        Long roleID,
        String username,
        String passwordHash,
        String fullName,
        String email,
        String phone,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
