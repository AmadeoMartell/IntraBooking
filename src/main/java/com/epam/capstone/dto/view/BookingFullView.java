package com.epam.capstone.dto.view;

import com.epam.capstone.dto.*;

/**
 * Simple holder for combining a BookingDto with its UserDto, RoomDto, LocationDto and StatusDto.
 */
public record BookingFullView(
        BookingDto booking,
        UserDto user,
        RoomDto room,
        LocationDto location,
        StatusDto status
) {}
