package com.epam.capstone.dto.view;

import com.epam.capstone.dto.BookingDto;
import com.epam.capstone.dto.LocationDto;
import com.epam.capstone.dto.RoomDto;
import com.epam.capstone.dto.StatusDto;

/**
 * Simple holder for combining a BookingDto with its RoomDto, LocationDto and StatusDto.
 */
public record BookingView(
        BookingDto booking,
        RoomDto room,
        LocationDto location,
        StatusDto status
) {}
