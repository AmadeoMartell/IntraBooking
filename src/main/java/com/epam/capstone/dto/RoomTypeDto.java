package com.epam.capstone.dto;

public record RoomTypeDto(
        Integer typeId,
        String name,
        Integer capacity,
        String description
) {}
