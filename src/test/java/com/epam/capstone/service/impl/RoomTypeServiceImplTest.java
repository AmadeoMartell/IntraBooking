package com.epam.capstone.service.impl;

import com.epam.capstone.dto.RoomTypeDto;
import com.epam.capstone.dto.mapper.RoomTypeMapper;
import com.epam.capstone.exception.NotFoundException;
import com.epam.capstone.model.RoomType;
import com.epam.capstone.repository.RoomTypeRepository;
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
class RoomTypeServiceImplTest {

    @Mock
    private RoomTypeRepository roomTypeRepository;

    @Mock
    private RoomTypeMapper roomTypeMapper;

    @InjectMocks
    private RoomTypeServiceImpl service;

    private RoomTypeDto dummyDto;
    private RoomType dummyEntity;

    @BeforeEach
    void setUp() {
        dummyDto = mock(RoomTypeDto.class);
        dummyEntity = new RoomType();
    }

    @Test
    void createRoomType_success() {
        RoomTypeDto inDto = mock(RoomTypeDto.class);
        RoomType toSave = new RoomType();
        RoomType saved = new RoomType();
        saved.setTypeId(42);
        RoomTypeDto outDto = mock(RoomTypeDto.class);

        when(roomTypeMapper.toEntity(inDto)).thenReturn(toSave);
        when(roomTypeRepository.save(toSave)).thenReturn(saved);
        when(roomTypeMapper.toDto(saved)).thenReturn(outDto);

        RoomTypeDto result = service.createRoomType(inDto);

        assertSame(outDto, result);
        verify(roomTypeMapper).toEntity(inDto);
        verify(roomTypeRepository).save(toSave);
        verify(roomTypeMapper).toDto(saved);
    }

    @Test
    void getRoomTypeById_found() {
        RoomType found = new RoomType();
        RoomTypeDto outDto = mock(RoomTypeDto.class);

        when(roomTypeRepository.findById(7)).thenReturn(Optional.of(found));
        when(roomTypeMapper.toDto(found)).thenReturn(outDto);

        RoomTypeDto result = service.getRoomTypeById(7);

        assertSame(outDto, result);
    }

    @Test
    void getRoomTypeById_notFound() {
        when(roomTypeRepository.findById(anyInt())).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> service.getRoomTypeById(99));
    }

    @Test
    void getAvailableRoomTypesForLocation_mapsList() {
        List<RoomType> list = List.of(new RoomType(), new RoomType());
        when(roomTypeRepository.findByLocationId(5L)).thenReturn(list);
        when(roomTypeMapper.toDto(any(RoomType.class))).thenReturn(dummyDto);

        List<RoomTypeDto> result = service.getAvailableRoomTypesForLocation(5L);

        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(d -> d == dummyDto));
        verify(roomTypeRepository).findByLocationId(5L);
        verify(roomTypeMapper, times(2)).toDto(any(RoomType.class));
    }

    @Test
    void getAllRoomTypes_listMaps() {
        List<RoomType> list = List.of(new RoomType());
        when(roomTypeRepository.findAll()).thenReturn(list);
        when(roomTypeMapper.toDto(any(RoomType.class))).thenReturn(dummyDto);

        List<RoomTypeDto> result = service.getAllRoomTypes();

        assertEquals(1, result.size());
        assertEquals(dummyDto, result.get(0));
        verify(roomTypeRepository).findAll();
        verify(roomTypeMapper).toDto(any(RoomType.class));
    }

    @Test
    void getAllRoomTypes_pageMaps() {
        Pageable pg = PageRequest.of(0, 3);
        RoomType rt = new RoomType();
        Page<RoomType> page = new PageImpl<>(List.of(rt), pg, 1);

        when(roomTypeRepository.findAll(pg)).thenReturn(page);
        when(roomTypeMapper.toDto(rt)).thenReturn(dummyDto);

        Page<RoomTypeDto> result = service.getAllRoomTypes(pg);

        assertEquals(1, result.getTotalElements());
        assertEquals(List.of(dummyDto), result.getContent());
        verify(roomTypeRepository).findAll(pg);
        verify(roomTypeMapper).toDto(rt);
    }

    @Test
    void updateRoomType_success() {
        RoomTypeDto updateDto = mock(RoomTypeDto.class);
        RoomType existing = new RoomType();
        existing.setTypeId(8);
        RoomTypeDto outDto = mock(RoomTypeDto.class);

        when(roomTypeRepository.findById(8)).thenReturn(Optional.of(existing));
        when(roomTypeRepository.save(existing)).thenReturn(existing);
        when(roomTypeMapper.toDto(existing)).thenReturn(outDto);

        RoomTypeDto result = service.updateRoomType(8, updateDto);

        assertSame(outDto, result);
        verify(roomTypeRepository).findById(8);
        verify(roomTypeMapper).partialUpdate(updateDto, existing);
        verify(roomTypeRepository).save(existing);
        verify(roomTypeMapper).toDto(existing);
    }

    @Test
    void updateRoomType_notFound() {
        when(roomTypeRepository.findById(anyInt())).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class,
                () -> service.updateRoomType(10, dummyDto));
    }

    @Test
    void deleteRoomType_success() {
        when(roomTypeRepository.existsById(3)).thenReturn(true);

        service.deleteRoomType(3);

        verify(roomTypeRepository).existsById(3);
        verify(roomTypeRepository).deleteById(3);
    }

    @Test
    void deleteRoomType_notFound() {
        when(roomTypeRepository.existsById(anyInt())).thenReturn(false);
        assertThrows(NotFoundException.class,
                () -> service.deleteRoomType(4));
    }
}
