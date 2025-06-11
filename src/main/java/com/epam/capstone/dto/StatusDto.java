package com.epam.capstone.dto;

/**
 * Data transfer object for a status record.
 *
 * @param statusId unique identifier of the status
 * @param name     name of the status (e.g., PENDING, APPROVED)
 */
public record StatusDto(
        Short statusId,
        String name
) {}
