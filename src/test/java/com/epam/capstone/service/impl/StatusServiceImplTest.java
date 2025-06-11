package com.epam.capstone.service.impl;

import com.epam.capstone.dto.StatusDto;
import com.epam.capstone.dto.mapper.StatusMapper;
import com.epam.capstone.exception.NotFoundException;
import com.epam.capstone.model.Status;
import com.epam.capstone.repository.StatusRepository;
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
class StatusServiceImplTest {

    @Mock
    private StatusRepository statusRepository;

    @Mock
    private StatusMapper statusMapper;

    @InjectMocks
    private StatusServiceImpl service;

    private StatusDto dummyDto;
    private Status dummyEntity;

    @BeforeEach
    void setUp() {
        dummyDto = mock(StatusDto.class);
        dummyEntity = new Status();
    }

    @Test
    void createStatus_success() {
        StatusDto inDto = mock(StatusDto.class);
        Status toSave = new Status();
        Status saved = new Status();
        saved.setStatusId((short)7);
        StatusDto outDto = mock(StatusDto.class);

        when(statusMapper.toEntity(inDto)).thenReturn(toSave);
        when(statusRepository.save(toSave)).thenReturn(saved);
        when(statusMapper.toDto(saved)).thenReturn(outDto);

        StatusDto result = service.createStatus(inDto);

        assertSame(outDto, result);
        verify(statusMapper).toEntity(inDto);
        verify(statusRepository).save(toSave);
        verify(statusMapper).toDto(saved);
    }

    @Test
    void getStatusById_found() {
        Status found = new Status();
        found.setStatusId((short)3);
        StatusDto outDto = mock(StatusDto.class);

        when(statusRepository.findById((short)3)).thenReturn(Optional.of(found));
        when(statusMapper.toDto(found)).thenReturn(outDto);

        StatusDto result = service.getStatusById((short)3);

        assertSame(outDto, result);
    }

    @Test
    void getStatusById_notFound() {
        when(statusRepository.findById(anyShort())).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> service.getStatusById((short)5));
    }

    @Test
    void getAllStatuses_pageMaps() {
        Pageable pg = PageRequest.of(0, 2);
        Status s1 = new Status(), s2 = new Status();
        Page<Status> page = new PageImpl<>(List.of(s1, s2), pg, 2);

        when(statusRepository.findAll(pg)).thenReturn(page);
        when(statusMapper.toDto(any(Status.class))).thenReturn(dummyDto);

        Page<StatusDto> result = service.getAllStatuses(pg);

        assertEquals(2, result.getTotalElements());
        assertTrue(result.getContent().stream().allMatch(d -> d == dummyDto));
        verify(statusRepository).findAll(pg);
        verify(statusMapper, times(2)).toDto(any(Status.class));
    }

    @Test
    void findAll_listMaps() {
        Status s = new Status();
        when(statusRepository.findAll()).thenReturn(List.of(s, s));
        when(statusMapper.toDto(any(Status.class))).thenReturn(dummyDto);

        List<StatusDto> result = service.findAll();

        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(d -> d == dummyDto));
        verify(statusRepository).findAll();
        verify(statusMapper, times(2)).toDto(any(Status.class));
    }

    @Test
    void updateStatus_success() {
        StatusDto updateDto = mock(StatusDto.class);
        Status existing = new Status();
        existing.setStatusId((short)9);
        StatusDto outDto = mock(StatusDto.class);

        when(statusRepository.findById((short)9)).thenReturn(Optional.of(existing));
        when(statusRepository.save(existing)).thenReturn(existing);
        when(statusMapper.toDto(existing)).thenReturn(outDto);

        StatusDto result = service.updateStatus((short)9, updateDto);

        assertSame(outDto, result);
        verify(statusRepository).findById((short)9);
        verify(statusMapper).partialUpdate(updateDto, existing);
        verify(statusRepository).save(existing);
        verify(statusMapper).toDto(existing);
    }

    @Test
    void updateStatus_notFound() {
        when(statusRepository.findById(anyShort())).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> service.updateStatus((short)4, dummyDto));
    }

    @Test
    void deleteStatus_success() {
        when(statusRepository.existsById((short)2)).thenReturn(true);

        service.deleteStatus((short)2);

        verify(statusRepository).existsById((short)2);
        verify(statusRepository).deleteById((short)2);
    }

    @Test
    void deleteStatus_notFound() {
        when(statusRepository.existsById(anyShort())).thenReturn(false);
        assertThrows(NotFoundException.class, () -> service.deleteStatus((short)6));
    }

    @Test
    void getStatusByName_found() {
        Status s = new Status();
        StatusDto outDto = mock(StatusDto.class);

        when(statusRepository.findByName("ACTIVE")).thenReturn(Optional.of(s));
        when(statusMapper.toDto(s)).thenReturn(outDto);

        StatusDto result = service.getStatusByName("ACTIVE");

        assertSame(outDto, result);
    }

    @Test
    void getStatusByName_notFound() {
        when(statusRepository.findByName(anyString())).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> service.getStatusByName("UNKNOWN"));
    }
}
