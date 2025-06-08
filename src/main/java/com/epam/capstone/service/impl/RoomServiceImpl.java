package com.epam.capstone.service.impl;

import com.epam.capstone.dto.RoomDto;
import com.epam.capstone.dto.mapper.RoomMapper;
import com.epam.capstone.exception.NotFoundException;
import com.epam.capstone.model.Room;
import com.epam.capstone.repository.RoomRepository;
import com.epam.capstone.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Implementation of {@link RoomService} that manages rooms via RoomRepository and maps entities to DTOs.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RoomServiceImpl implements RoomService {

    private final RoomRepository roomRepository;
    private final RoomMapper roomMapper;

    @Override
    @Transactional
    public RoomDto createRoom(RoomDto roomDto) {
        Room entity = roomMapper.toEntity(roomDto);
        Room saved = roomRepository.save(entity);
        return roomMapper.toDto(saved);
    }

    @Override
    public RoomDto getRoomById(Long roomId) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new NotFoundException("Room with id=" + roomId + " not found"));
        return roomMapper.toDto(room);
    }

    @Override
    public Page<RoomDto> getAllRooms(Pageable pageable) {
        Page<Room> page = roomRepository.findAll(pageable);
        return page.map(roomMapper::toDto);
    }

    @Override
    @Transactional
    public RoomDto updateRoom(Long roomId, RoomDto roomDto) {
        Room existing = roomRepository.findById(roomId)
                .orElseThrow(() -> new NotFoundException("Room with id=" + roomId + " not found"));
        roomMapper.partialUpdate(roomDto, existing);
        Room updated = roomRepository.save(existing);
        return roomMapper.toDto(updated);
    }

    @Override
    @Transactional
    public void deleteRoom(Long roomId) {
        if (!roomRepository.existsById(roomId)) {
            throw new NotFoundException("Room with id=" + roomId + " not found");
        }
        roomRepository.deleteById(roomId);
    }

    @Override
    public Page<RoomDto> getRoomsByLocation(Long locationId, Pageable pageable) {
        Page<Room> page = roomRepository.findByLocationId(locationId, pageable);
        return page.map(roomMapper::toDto);
    }

    @Override
    public Page<RoomDto> getRoomsByType(Integer typeId, Pageable pageable) {
        Page<Room> page = roomRepository.findByTypeId(typeId, pageable);
        return page.map(roomMapper::toDto);
    }

    @Override
    public long countRoomsByLocationAndType(Long locationId, Long typeId) {
        return roomRepository.countByLocationAndType(locationId, typeId);
    }

    @Override
    public Page<RoomDto> findRoomsByLocationAndTypeAndName(
            Long locationId,
            Integer typeId,
            String nameFilter,
            Pageable pageable
    ) {
        Page<Room> roomPage = roomRepository.findByLocationAndTypeAndName(
                locationId,
                typeId,
                nameFilter,
                pageable
        );
        return roomPage.map(roomMapper::toDto);
    }

    @Override
    public List<RoomDto> getRoomsByLocationAndType(Long locationId, Integer typeId) {
        List<Room> rooms = roomRepository.findRoomsListByLocationAndType(locationId, typeId);
        return rooms.stream()
                .map(roomMapper::toDto)
                .toList();
    }
}
