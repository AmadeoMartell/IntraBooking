package com.epam.capstone.service.impl;

import com.epam.capstone.dto.UserDto;
import com.epam.capstone.dto.mapper.UserMapper;
import com.epam.capstone.exception.AlreadyExistException;
import com.epam.capstone.exception.NotFoundException;
import com.epam.capstone.model.User;
import com.epam.capstone.repository.UserRepository;
import com.epam.capstone.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Implementation of {@link UserService} that manages users via UserRepository and maps entities to DTOs.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    @Transactional
    public UserDto createUser(UserDto userDto) {
        User entity = userMapper.toEntity(userDto);
        userRepository.selectByUsernameOrEmail(entity.getUsername(), entity.getEmail())
                .ifPresent(user -> {
                    throw new AlreadyExistException("User with username or email=" + user.getUsername() + " or " + user.getEmail() + " already exist!");
                });
        User saved = userRepository.save(entity);
        return userMapper.toDto(saved);
    }

    @Override
    public UserDto getUserById(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id=" + userId + " not found"));
        return userMapper.toDto(user);
    }

    @Override
    public Page<UserDto> getAllUsers(Pageable pageable) {
        Page<User> page = userRepository.findAll(pageable);
        return page.map(userMapper::toDto);
    }

    @Override
    @Transactional
    public UserDto updateUser(Long userId, UserDto userDto) {
        User existing = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id=" + userId + " not found"));
        userMapper.partialUpdate(userDto, existing);
        User updated = userRepository.save(existing);
        return userMapper.toDto(updated);
    }

    @Override
    @Transactional
    public void deleteUser(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("User with id=" + userId + " not found");
        }
        userRepository.deleteById(userId);
    }

    @Override
    @Transactional
    public void deleteUsers(List<Long> userIds) {
        for (Long userId : userIds) {
            if (!userRepository.existsById(userId)) {
                throw new NotFoundException("User with id=" + userId + " not found");
            }
            userRepository.deleteById(userId);
        }
    }

    @Override
    public Page<UserDto> findUsersByName(String nameFilter, Pageable pageable) {
        var users = userRepository.findByFullNameContaining(nameFilter, pageable);
        return users.map(userMapper::toDto);
    }
}
