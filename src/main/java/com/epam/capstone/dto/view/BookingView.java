package com.epam.capstone.dto.view;

import com.epam.capstone.dto.BookingDto;
import com.epam.capstone.dto.LocationDto;
import com.epam.capstone.dto.RoomDto;
import com.epam.capstone.dto.StatusDto;

/**
 * Simple holder for combining a BookingDto with its RoomDto, LocationDto, and StatusDto.
 *
 * @param booking  the booking data transfer object
 * @param room     the associated room data transfer object
 * @param location the associated location data transfer object
 * @param status   the associated status data transfer object
 */
public record BookingView(
        BookingDto booking,
        RoomDto room,
        LocationDto location,
        StatusDto status
) {}
