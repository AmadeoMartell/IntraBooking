package com.epam.capstone.service.impl;

import com.epam.capstone.dto.RoleDto;
import com.epam.capstone.dto.mapper.RoleMapper;
import com.epam.capstone.exception.NotFoundException;
import com.epam.capstone.model.Role;
import com.epam.capstone.repository.RoleRepository;
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
class RoleServiceImplTest {

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private RoleMapper roleMapper;

    @InjectMocks
    private RoleServiceImpl service;

    private RoleDto dummyDto;
    private Role dummyEntity;

    @BeforeEach
    void setUp() {
        dummyDto = mock(RoleDto.class);
        dummyEntity = new Role();
    }

    @Test
    void createRole_success() {
        Role toSave = new Role();
        Role saved = new Role();
        saved.setRoleId(5L);
        RoleDto outDto = mock(RoleDto.class);

        when(roleMapper.toEntity(dummyDto)).thenReturn(toSave);
        when(roleRepository.save(toSave)).thenReturn(saved);
        when(roleMapper.toDto(saved)).thenReturn(outDto);

        RoleDto result = service.createRole(dummyDto);

        assertSame(outDto, result);
        verify(roleMapper).toEntity(dummyDto);
        verify(roleRepository).save(toSave);
        verify(roleMapper).toDto(saved);
    }

    @Test
    void getRoleById_found() {
        Role found = new Role();
        RoleDto outDto = mock(RoleDto.class);

        when(roleRepository.findById(1L)).thenReturn(Optional.of(found));
        when(roleMapper.toDto(found)).thenReturn(outDto);

        RoleDto result = service.getRoleById(1L);

        assertSame(outDto, result);
    }

    @Test
    void getRoleById_notFound() {
        when(roleRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> service.getRoleById(99L));
    }

    @Test
    void getAllRoles_pageMapping() {
        Pageable pg = PageRequest.of(0, 3);
        Role r1 = new Role(), r2 = new Role();
        Page<Role> page = new PageImpl<>(List.of(r1, r2), pg, 2);
        when(roleRepository.findAll(pg)).thenReturn(page);
        when(roleMapper.toDto(any(Role.class))).thenReturn(dummyDto);

        Page<RoleDto> result = service.getAllRoles(pg);

        assertEquals(2, result.getTotalElements());
        assertTrue(result.getContent().stream().allMatch(d -> d == dummyDto));
        verify(roleRepository).findAll(pg);
        verify(roleMapper, times(2)).toDto(any(Role.class));
    }

    @Test
    void findAll_listMapping() {
        Role r1 = new Role(), r2 = new Role();
        when(roleRepository.findAll()).thenReturn(List.of(r1, r2));
        when(roleMapper.toDto(any(Role.class))).thenReturn(dummyDto);

        List<RoleDto> result = service.findAll();

        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(d -> d == dummyDto));
        verify(roleRepository).findAll();
        verify(roleMapper, times(2)).toDto(any(Role.class));
    }

    @Test
    void updateRole_success() {
        RoleDto updateDto = mock(RoleDto.class);
        Role existing = new Role();
        existing.setRoleId(7L);
        RoleDto outDto = mock(RoleDto.class);

        when(roleRepository.findById(7L)).thenReturn(Optional.of(existing));
        // partialUpdate is void by default
        when(roleRepository.save(existing)).thenReturn(existing);
        when(roleMapper.toDto(existing)).thenReturn(outDto);

        RoleDto result = service.updateRole(7L, updateDto);

        assertSame(outDto, result);
        verify(roleRepository).findById(7L);
        verify(roleMapper).partialUpdate(updateDto, existing);
        verify(roleRepository).save(existing);
        verify(roleMapper).toDto(existing);
    }

    @Test
    void updateRole_notFound() {
        when(roleRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class,
                () -> service.updateRole(8L, dummyDto));
    }

    @Test
    void deleteRole_success() {
        when(roleRepository.existsById(4L)).thenReturn(true);

        service.deleteRole(4L);

        verify(roleRepository).existsById(4L);
        verify(roleRepository).deleteById(4L);
    }

    @Test
    void deleteRole_notFound() {
        when(roleRepository.existsById(anyLong())).thenReturn(false);
        assertThrows(NotFoundException.class,
                () -> service.deleteRole(5L));
    }
}
