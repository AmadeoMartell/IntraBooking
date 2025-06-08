package com.epam.capstone.service;

import com.epam.capstone.dto.RoomTypeDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Service interface for managing room types.
 */
public interface RoomTypeService {

    /**
     * Create a new room type.
     *
     * @param roomTypeDto DTO containing room type data
     * @return DTO of the created room type
     */
    RoomTypeDto createRoomType(RoomTypeDto roomTypeDto);

    /**
     * Retrieve a room type by its ID.
     *
     * @param typeId ID of the room type
     * @return DTO of the found room type
     */
    RoomTypeDto getRoomTypeById(Integer typeId);

    /**
     * Retrieve only those room types which have at least one room in the given location.
     *
     * @param locationId the ID of the location
     * @return list of available RoomTypeDto
     */
    List<RoomTypeDto> getAvailableRoomTypesForLocation(Long locationId);

    /**
     * Retrieve all room types (no pagination).
     */
    List<RoomTypeDto> getAllRoomTypes();

    /**
     * Retrieve all room types with pagination.
     *
     * @param pageable pagination information
     * @return page of room type DTOs
     */
    Page<RoomTypeDto> getAllRoomTypes(Pageable pageable);

    /**
     * Update an existing room type.
     *
     * @param typeId      ID of the room type to update
     * @param roomTypeDto DTO containing new values
     * @return DTO of the updated room type
     */
    RoomTypeDto updateRoomType(Integer typeId, RoomTypeDto roomTypeDto);

    /**
     * Delete a room type by its ID.
     *
     * @param typeId ID of the room type to delete
     */
    void deleteRoomType(Integer typeId);
}
