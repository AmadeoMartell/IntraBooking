package com.epam.capstone.dto;

/**
 * Data transfer object for a room record.
 *
 * @param roomId      unique identifier of the room
 * @param locationId  identifier of the location where the room exists
 * @param typeId      identifier of the room's type
 * @param name        name or number of the room
 * @param capacity    capacity of the room
 * @param description details or description of the room
 */
public record RoomDto(
        Long roomId,
        Long locationId,
        Integer typeId,
        String name,
        Integer capacity,
        String description
) {}
