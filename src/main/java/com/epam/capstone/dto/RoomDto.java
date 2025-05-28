package com.epam.capstone.dto;

public record RoomDto(
        Long roomId,
        Long locationId,
        Integer typeId,
        String name,
        Integer capacity,
        String description
) {}
