package com.epam.capstone.service;

import com.epam.capstone.dto.RoleDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Service interface for managing roles.
 */
public interface RoleService {

    /**
     * Create a new role.
     *
     * @param roleDto DTO containing role data
     * @return DTO of the created role
     */
    RoleDto createRole(RoleDto roleDto);

    /**
     * Retrieve a role by its ID.
     *
     * @param roleId ID of the role
     * @return DTO of the found role
     */
    RoleDto getRoleById(Long roleId);

    /**
     * Retrieve all roles with pagination.
     *
     * @param pageable pagination information
     * @return page of role DTOs
     */
    Page<RoleDto> getAllRoles(Pageable pageable);

    /**
     * Retrieve all roles.
     *
     * @return list of role DTOs
     */
    List<RoleDto> findAll();

    /**
     * Update an existing role.
     *
     * @param roleId  ID of the role to update
     * @param roleDto DTO containing new values
     * @return DTO of the updated role
     */
    RoleDto updateRole(Long roleId, RoleDto roleDto);

    /**
     * Delete a role by its ID.
     *
     * @param roleId ID of the role to delete
     */
    void deleteRole(Long roleId);
}
