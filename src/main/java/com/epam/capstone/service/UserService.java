package com.epam.capstone.service;

import com.epam.capstone.dto.UserDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

/**
 * Service interface for managing users.
 */
public interface UserService {

    /**
     * Create a new user.
     *
     * @param userDto DTO containing user data
     * @return DTO of the created user
     */
    UserDto createUser(UserDto userDto);

    /**
     * Retrieve a user by its ID.
     *
     * @param userId ID of the user
     * @return DTO of the found user
     */
    UserDto getUserById(Long userId);

    /**
     * Retrieve all users with pagination.
     *
     * @param pageable pagination information
     * @return page of user DTOs
     */
    Page<UserDto> getAllUsers(Pageable pageable);

    /**
     * Update an existing user.
     *
     * @param userId  ID of the user to update
     * @param userDto DTO containing new values
     * @return DTO of the updated user
     */
    UserDto updateUser(Long userId, UserDto userDto);

    /**
     * Delete a user by its ID.
     *
     * @param userId ID of the user to delete
     */
    void deleteUser(Long userId);

    /**
     * Delete a user by its ID.
     *
     * @param userIds list of user Id's to delete
     */
    void deleteUsers(List<Long> userIds);

    /**
     * Retrieve all users by name with pagination.
     * @param nameFilter nameFilter for search
     * @param pageable pagination information
     * @return page of user DTOs
     */
    Page<UserDto> findUsersByName(String nameFilter, Pageable pageable);
}
