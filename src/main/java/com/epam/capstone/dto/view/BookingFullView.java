package com.epam.capstone.dto.view;

import com.epam.capstone.dto.*;

/**
 * Simple holder for combining a BookingDto with its UserDto, RoomDto, LocationDto, and StatusDto.
 *
 * @param booking  the booking data transfer object
 * @param user     the user data transfer object
 * @param room     the associated room data transfer object
 * @param location the associated location data transfer object
 * @param status   the associated status data transfer object
 */
public record BookingFullView(
        BookingDto booking,
        UserDto user,
        RoomDto room,
        LocationDto location,
        StatusDto status
) {}
