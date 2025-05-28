package com.epam.capstone.dto;

public record LocationDto(
        Long locationId,
        String name,
        String address,
        String description
) {}
