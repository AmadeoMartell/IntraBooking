package com.epam.capstone.service;

import com.epam.capstone.dto.StatusDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Service interface for managing statuses.
 */
public interface StatusService {

    /**
     * Create a new status.
     *
     * @param statusDto DTO containing status data
     * @return DTO of the created status
     */
    StatusDto createStatus(StatusDto statusDto);

    /**
     * Retrieve a status by its ID.
     *
     * @param statusId ID of the status
     * @return DTO of the found status
     */
    StatusDto getStatusById(Short statusId);

    /**
     * Retrieve all statuses with pagination.
     *
     * @param pageable pagination information
     * @return page of status DTOs
     */
    Page<StatusDto> getAllStatuses(Pageable pageable);

    /**
     * Retrieve all statuses.
     *
     * @return List of status DTOs
     */
    List<StatusDto> findAll();

    /**
     * Update an existing status.
     *
     * @param statusId  ID of the status to update
     * @param statusDto DTO containing new values
     * @return DTO of the updated status
     */
    StatusDto updateStatus(Short statusId, StatusDto statusDto);

    /**
     * Delete a status by its ID.
     *
     * @param statusId ID of the status to delete
     */
    void deleteStatus(Short statusId);

    StatusDto getStatusByName(String name);
}
