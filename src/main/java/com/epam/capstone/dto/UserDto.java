package com.epam.capstone.dto;

import java.time.LocalDateTime;

/**
 * Data transfer object for a user record.
 *
 * @param userId        unique identifier of the user
 * @param roleId        identifier of the user's role
 * @param username      login name of the user
 * @param passwordHash  hashed password of the user
 * @param fullName      full name of the user
 * @param email         email address of the user
 * @param phone         phone number of the user
 * @param createdAt     timestamp when the user was created
 * @param updatedAt     timestamp when the user was last updated
 */
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
