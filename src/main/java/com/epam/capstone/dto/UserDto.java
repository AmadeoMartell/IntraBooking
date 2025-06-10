package com.epam.capstone.dto;

import java.time.LocalDateTime;

public record UserDto(
        Long userId,
        Long roleId,
        String username,
        String passwordHash,
        String fullName,
        String email,
        String phone,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}
