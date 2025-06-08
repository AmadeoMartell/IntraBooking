package com.epam.capstone.service;

import com.epam.capstone.dto.LocationDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Service interface for managing locations.
 */
public interface LocationService {

    /**
     * Create a new location.
     *
     * @param locationDto DTO containing location data
     * @return DTO of the created location
     */
    LocationDto createLocation(LocationDto locationDto);

    /**
     * Retrieve a location by its ID.
     *
     * @param locationId ID of the location
     * @return DTO of the found location
     */
    LocationDto getLocationById(Long locationId);

    /**
     * Retrieve all locations with pagination.
     *
     * @param pageable pagination information
     * @return page of location DTOs
     */
    Page<LocationDto> getAllLocations(Pageable pageable);

    /**
     * Retrieve all locations as List<LocationDto>.
     *
     * @return list of locations DTOs
     */
    List<LocationDto> getAllLocations();

    /**
     * Update an existing location.
     *
     * @param locationId  ID of the location to update
     * @param locationDto DTO containing new values
     * @return DTO of the updated location
     */
    LocationDto updateLocation(Long locationId, LocationDto locationDto);

    /**
     * Delete a location by its ID.
     *
     * @param locationId ID of the location to delete
     */
    void deleteLocation(Long locationId);
}
