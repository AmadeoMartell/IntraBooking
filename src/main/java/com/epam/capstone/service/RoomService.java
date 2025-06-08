package com.epam.capstone.service;

import com.epam.capstone.dto.RoomDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Service interface for managing rooms.
 */
public interface RoomService {

    /**
     * Create a new room.
     *
     * @param roomDto DTO containing room data
     * @return DTO of the created room
     */
    RoomDto createRoom(RoomDto roomDto);

    /**
     * Retrieve a room by its ID.
     *
     * @param roomId ID of the room
     * @return DTO of the found room
     */
    RoomDto getRoomById(Long roomId);

    /**
     * Retrieve all rooms with pagination.
     *
     * @param pageable pagination information
     * @return page of room DTOs
     */
    Page<RoomDto> getAllRooms(Pageable pageable);

    /**
     * Update an existing room.
     *
     * @param roomId  ID of the room to update
     * @param roomDto DTO containing new values
     * @return DTO of the updated room
     */
    RoomDto updateRoom(Long roomId, RoomDto roomDto);

    /**
     * Delete a room by its ID.
     *
     * @param roomId ID of the room to delete
     */
    void deleteRoom(Long roomId);

    /**
     * Retrieve rooms filtered by location ID.
     *
     * @param locationId ID of the location
     * @param pageable   pagination information
     * @return page of room DTOs
     */
    Page<RoomDto> getRoomsByLocation(Long locationId, Pageable pageable);

    /**
     * Retrieve rooms filtered by room type ID.
     *
     * @param typeId   ID of the room type
     * @param pageable pagination information
     * @return page of room DTOs
     */
    Page<RoomDto> getRoomsByType(Integer typeId, Pageable pageable);


    long countRoomsByLocationAndType(Long locationId, Long typeId);

    /**
     * Retrieve rooms filtered by location ID.
     *
     * @param locationId ID of the location
     * @param typeId ID of the roomType
     * @param nameFilter filter for find room by name
     * @param pageable   pagination information
     * @return page of room DTOs
     */
    Page<RoomDto> findRoomsByLocationAndTypeAndName(
            Long locationId,
            Integer typeId,
            String nameFilter,
            Pageable pageable
    );

    List<RoomDto> getRoomsByLocationAndType(Long locationId, Integer typeId);
}
