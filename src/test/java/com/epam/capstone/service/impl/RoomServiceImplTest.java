package com.epam.capstone.service.impl;

import com.epam.capstone.dto.RoomDto;
import com.epam.capstone.dto.mapper.RoomMapper;
import com.epam.capstone.exception.NotFoundException;
import com.epam.capstone.model.Room;
import com.epam.capstone.repository.RoomRepository;
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
class RoomServiceImplTest {

    @Mock
    private RoomRepository roomRepository;

    @Mock
    private RoomMapper roomMapper;

    @InjectMocks
    private RoomServiceImpl service;

    private RoomDto dummyDto;
    private Room dummyEntity;

    @BeforeEach
    void setUp() {
        dummyDto = mock(RoomDto.class);
        dummyEntity = new Room();
    }

    @Test
    void createRoom_success() {
        RoomDto inDto = mock(RoomDto.class);
        Room toSave = new Room();
        Room saved = new Room();
        saved.setRoomId(123L);
        RoomDto outDto = mock(RoomDto.class);

        when(roomMapper.toEntity(inDto)).thenReturn(toSave);
        when(roomRepository.save(toSave)).thenReturn(saved);
        when(roomMapper.toDto(saved)).thenReturn(outDto);

        RoomDto result = service.createRoom(inDto);

        assertSame(outDto, result);
        verify(roomMapper).toEntity(inDto);
        verify(roomRepository).save(toSave);
        verify(roomMapper).toDto(saved);
    }

    @Test
    void getRoomById_found() {
        Room found = new Room();
        RoomDto outDto = mock(RoomDto.class);

        when(roomRepository.findById(1L)).thenReturn(Optional.of(found));
        when(roomMapper.toDto(found)).thenReturn(outDto);

        RoomDto result = service.getRoomById(1L);

        assertSame(outDto, result);
    }

    @Test
    void getRoomById_notFound() {
        when(roomRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> service.getRoomById(99L));
    }

    @Test
    void getAllRooms_pageMaps() {
        Pageable pg = PageRequest.of(0, 2);
        Room r1 = new Room(), r2 = new Room();
        Page<Room> page = new PageImpl<>(List.of(r1, r2), pg, 2);

        when(roomRepository.findAll(pg)).thenReturn(page);
        when(roomMapper.toDto(any(Room.class))).thenReturn(dummyDto);

        Page<RoomDto> result = service.getAllRooms(pg);

        assertEquals(2, result.getTotalElements());
        assertTrue(result.getContent().stream().allMatch(d -> d == dummyDto));
        verify(roomRepository).findAll(pg);
        verify(roomMapper, times(2)).toDto(any(Room.class));
    }

    @Test
    void updateRoom_success() {
        RoomDto updateDto = mock(RoomDto.class);
        Room existing = new Room();
        existing.setRoomId(5L);
        RoomDto outDto = mock(RoomDto.class);

        when(roomRepository.findById(5L)).thenReturn(Optional.of(existing));
        when(roomRepository.save(existing)).thenReturn(existing);
        when(roomMapper.toDto(existing)).thenReturn(outDto);

        RoomDto result = service.updateRoom(5L, updateDto);

        assertSame(outDto, result);
        verify(roomMapper).partialUpdate(updateDto, existing);
        verify(roomRepository).save(existing);
    }

    @Test
    void updateRoom_notFound() {
        when(roomRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class,
                () -> service.updateRoom(1L, dummyDto));
    }

    @Test
    void deleteRoom_success() {
        when(roomRepository.existsById(7L)).thenReturn(true);

        service.deleteRoom(7L);

        verify(roomRepository).existsById(7L);
        verify(roomRepository).deleteById(7L);
    }

    @Test
    void deleteRoom_notFound() {
        when(roomRepository.existsById(anyLong())).thenReturn(false);
        assertThrows(NotFoundException.class, () -> service.deleteRoom(8L));
    }

    @Test
    void countRoomsByLocationAndType_delegates() {
        when(roomRepository.countByLocationAndType(5L, 6L)).thenReturn(8L);
        assertEquals(8L, service.countRoomsByLocationAndType(5L, 6L));
        verify(roomRepository).countByLocationAndType(5L, 6L);
    }

    @Test
    void findRoomsByLocationAndTypeAndName_blankFilter() {
        Pageable pg = PageRequest.of(0, 5);
        Room r = new Room();
        Page<Room> page = new PageImpl<>(List.of(r), pg, 1);

        when(roomRepository.findByLocationAndTypeAndName(4L, 5, "  ", pg))
                .thenReturn(page);
        when(roomMapper.toDto(r)).thenReturn(dummyDto);

        Page<RoomDto> result =
                service.findRoomsByLocationAndTypeAndName(4L, 5, "  ", pg);

        assertEquals(1, result.getTotalElements());
        assertEquals(List.of(dummyDto), result.getContent());
    }

    @Test
    void getRoomsByLocationAndType_listMaps() {
        List<Room> list = List.of(new Room(), new Room());
        when(roomRepository.findRoomsListByLocationAndType(7L, 8))
                .thenReturn(list);
        when(roomMapper.toDto(any(Room.class))).thenReturn(dummyDto);

        List<RoomDto> result = service.getRoomsByLocationAndType(7L, 8);

        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(d -> d == dummyDto));
        verify(roomRepository).findRoomsListByLocationAndType(7L, 8);
    }
}
