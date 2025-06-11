package com.epam.capstone.dto;

/**
 * Data transfer object for a room type record.
 *
 * @param typeId      unique identifier of the room type
 * @param name        name of the room type
 * @param capacity    maximum capacity of the room type
 * @param description description of the room type
 */
public record RoomTypeDto(
        Integer typeId,
        String name,
        Integer capacity,
        String description
) {}
