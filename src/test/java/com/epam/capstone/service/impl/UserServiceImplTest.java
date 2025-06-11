package com.epam.capstone.service.impl;

import com.epam.capstone.dto.UserDto;
import com.epam.capstone.dto.mapper.UserMapper;
import com.epam.capstone.exception.AlreadyExistException;
import com.epam.capstone.exception.NotFoundException;
import com.epam.capstone.model.User;
import com.epam.capstone.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserServiceImpl service;

    private UserDto dummyDto;
    private User dummyEntity;

    @BeforeEach
    void setUp() {
        dummyDto = mock(UserDto.class);
        dummyEntity = new User();
    }

    @Test
    void createUser_success() {
        UserDto inDto = mock(UserDto.class);
        User toSave = new User();
        toSave.setUsername("alice");
        toSave.setEmail("alice@example.com");
        User saved = new User();
        saved.setUserId(10L);
        saved.setUsername("alice");
        saved.setEmail("alice@example.com");
        UserDto outDto = mock(UserDto.class);

        when(userMapper.toEntity(inDto)).thenReturn(toSave);
        when(userRepository.selectByUsernameOrEmail("alice", "alice@example.com"))
                .thenReturn(Optional.empty());
        when(userRepository.save(toSave)).thenReturn(saved);
        when(userMapper.toDto(saved)).thenReturn(outDto);

        UserDto result = service.createUser(inDto);

        assertSame(outDto, result);
        verify(userMapper).toEntity(inDto);
        verify(userRepository).selectByUsernameOrEmail("alice", "alice@example.com");
        verify(userRepository).save(toSave);
        verify(userMapper).toDto(saved);
    }

    @Test
    void createUser_conflict() {
        UserDto inDto = mock(UserDto.class);
        User toSave = new User();
        toSave.setUsername("bob");
        toSave.setEmail("bob@example.com");
        User conflict = new User();
        conflict.setUserId(99L);
        conflict.setUsername("bob");
        conflict.setEmail("bob@example.com");

        when(userMapper.toEntity(inDto)).thenReturn(toSave);
        when(userRepository.selectByUsernameOrEmail("bob", "bob@example.com"))
                .thenReturn(Optional.of(conflict));

        AlreadyExistException ex = assertThrows(AlreadyExistException.class,
                () -> service.createUser(inDto));
        assertTrue(ex.getMessage().contains("already exist"));
        verify(userRepository, never()).save(any());
    }

    @Test
    void getUserById_found() {
        User u = new User();
        u.setUserId(5L);
        UserDto outDto = mock(UserDto.class);

        when(userRepository.findById(5L)).thenReturn(Optional.of(u));
        when(userMapper.toDto(u)).thenReturn(outDto);

        UserDto result = service.getUserById(5L);
        assertSame(outDto, result);
    }

    @Test
    void getUserById_notFound() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> service.getUserById(123L));
    }

    @Test
    void getAllUsers_mapsPage() {
        Pageable pg = PageRequest.of(0, 2);
        User u1 = new User(), u2 = new User();
        Page<User> page = new PageImpl<>(List.of(u1,u2), pg, 2);

        when(userRepository.findAll(pg)).thenReturn(page);
        when(userMapper.toDto(any(User.class))).thenReturn(dummyDto);

        Page<UserDto> result = service.getAllUsers(pg);

        assertEquals(2, result.getTotalElements());
        assertTrue(result.getContent().stream().allMatch(d -> d == dummyDto));
        verify(userRepository).findAll(pg);
        verify(userMapper, times(2)).toDto(any(User.class));
    }

    @Test
    void updateUser_success_noConflict() {
        UserDto inDto = mock(UserDto.class);
        User existing = new User();
        existing.setUserId(7L);
        existing.setUsername("charlie");
        existing.setEmail("c@example.com");
        UserDto outDto = mock(UserDto.class);

        when(userRepository.findById(7L)).thenReturn(Optional.of(existing));
        // partialUpdate does nothing by default
        when(userRepository.selectByUsernameOrEmail("charlie","c@example.com"))
                .thenReturn(Optional.of(existing)); // same user => no conflict
        when(userRepository.save(existing)).thenReturn(existing);
        when(userMapper.toDto(existing)).thenReturn(outDto);

        UserDto result = service.updateUser(7L, inDto);

        assertSame(outDto, result);
        verify(userMapper).partialUpdate(inDto, existing);
        verify(userRepository).save(existing);
    }

    @Test
    void updateUser_conflictDifferentUser() {
        UserDto inDto = mock(UserDto.class);
        User existing = new User();
        existing.setUserId(8L);
        existing.setUsername("dave");
        existing.setEmail("d@example.com");
        User other = new User();
        other.setUserId(9L);
        other.setUsername("dave");
        other.setEmail("d@example.com");

        when(userRepository.findById(8L)).thenReturn(Optional.of(existing));
        when(userRepository.selectByUsernameOrEmail("dave","d@example.com"))
                .thenReturn(Optional.of(other));

        assertThrows(AlreadyExistException.class,
                () -> service.updateUser(8L, inDto));
        verify(userRepository, never()).save(any());
    }

    @Test
    void updateUser_notFound() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class,
                () -> service.updateUser(1L, dummyDto));
    }

    @Test
    void deleteUser_success() {
        when(userRepository.existsById(4L)).thenReturn(true);

        service.deleteUser(4L);
        verify(userRepository).deleteById(4L);
    }

    @Test
    void deleteUser_notFound() {
        when(userRepository.existsById(anyLong())).thenReturn(false);
        assertThrows(NotFoundException.class,
                () -> service.deleteUser(5L));
    }

    @Test
    void deleteUsers_success() {
        List<Long> ids = List.of(1L,2L,3L);
        when(userRepository.existsById(anyLong())).thenReturn(true);

        service.deleteUsers(ids);
        verify(userRepository, times(3)).deleteById(anyLong());
    }

    @Test
    void deleteUsers_notFound() {
        when(userRepository.existsById(2L)).thenReturn(false);
        assertThrows(NotFoundException.class,
                () -> service.deleteUsers(List.of(2L,3L)));
    }
}
