package com.epam.capstone.service.impl;

import com.epam.capstone.dto.RoomTypeDto;
import com.epam.capstone.dto.mapper.RoomTypeMapper;
import com.epam.capstone.exception.NotFoundException;
import com.epam.capstone.model.RoomType;
import com.epam.capstone.repository.RoomTypeRepository;
import com.epam.capstone.service.RoomTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of {@link RoomTypeService} that manages room types via RoomTypeRepository and maps entities to DTOs.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RoomTypeServiceImpl implements RoomTypeService {

    private final RoomTypeRepository roomTypeRepository;
    private final RoomTypeMapper roomTypeMapper;

    @Override
    @Transactional
    public RoomTypeDto createRoomType(RoomTypeDto roomTypeDto) {
        RoomType entity = roomTypeMapper.toEntity(roomTypeDto);
        RoomType saved = roomTypeRepository.save(entity);
        return roomTypeMapper.toDto(saved);
    }

    @Override
    public RoomTypeDto getRoomTypeById(Integer typeId) {
        RoomType roomType = roomTypeRepository.findById(typeId)
                .orElseThrow(() -> new NotFoundException("RoomType with id=" + typeId + " not found"));
        return roomTypeMapper.toDto(roomType);
    }

    @Override
    public List<RoomTypeDto> getAvailableRoomTypesForLocation(Long locationId) {
        List<RoomType> types = roomTypeRepository.findByLocationId(locationId);
        return types.stream()
                .map(roomTypeMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<RoomTypeDto> getAllRoomTypes() {
        List<RoomType> roomType = roomTypeRepository.findAll();
        return roomType.stream().map(roomTypeMapper::toDto).collect(Collectors.toList());
    }

    @Override
    public Page<RoomTypeDto> getAllRoomTypes(Pageable pageable) {
        Page<RoomType> page = roomTypeRepository.findAll(pageable);
        return page.map(roomTypeMapper::toDto);
    }

    @Override
    @Transactional
    public RoomTypeDto updateRoomType(Integer typeId, RoomTypeDto roomTypeDto) {
        RoomType existing = roomTypeRepository.findById(typeId)
                .orElseThrow(() -> new NotFoundException("RoomType with id=" + typeId + " not found"));
        roomTypeMapper.partialUpdate(roomTypeDto, existing);
        RoomType updated = roomTypeRepository.save(existing);
        return roomTypeMapper.toDto(updated);
    }

    @Override
    @Transactional
    public void deleteRoomType(Integer typeId) {
        if (!roomTypeRepository.existsById(typeId)) {
            throw new NotFoundException("RoomType with id=" + typeId + " not found");
        }
        roomTypeRepository.deleteById(typeId);
    }

    @Override
    public Page<RoomTypeDto> findByName(String nameFilter, Pageable pageable) {
        Page<RoomType> roomTypes = roomTypeRepository.findByNameContaining(nameFilter, pageable);
        return roomTypes.map(roomTypeMapper::toDto);
    }
}
