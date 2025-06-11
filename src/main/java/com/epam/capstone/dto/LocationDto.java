package com.epam.capstone.dto;

/**
 * Data transfer object for a location record.
 *
 * @param locationId unique identifier of the location
 * @param name       name of the location
 * @param address    physical address of the location
 * @param description details or description of the location
 */
public record LocationDto(
        Long locationId,
        String name,
        String address,
        String description
) {}
