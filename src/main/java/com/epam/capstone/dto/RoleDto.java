package com.epam.capstone.dto;

/**
 * Data transfer object for a role record.
 *
 * @param roleId      unique identifier of the role
 * @param name        name of the role (e.g., ADMIN, READER)
 * @param description description of the role's purpose or permissions
 */
public record RoleDto(
        Long roleId,
        String name,
        String description
) {}
