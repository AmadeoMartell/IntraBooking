package com.epam.capstone.service.impl;

import com.epam.capstone.dto.LocationDto;
import com.epam.capstone.dto.mapper.LocationMapper;
import com.epam.capstone.exception.NotFoundException;
import com.epam.capstone.model.Location;
import com.epam.capstone.repository.LocationRepository;
import com.epam.capstone.service.LocationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of {@link LocationService} that manages Locations via LocationRepository and maps entities to DTOs.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class LocationServiceImpl implements LocationService {

    private final LocationRepository locationRepository;
    private final LocationMapper locationMapper;

    @Override
    @Transactional
    public LocationDto createLocation(LocationDto locationDto) {
        Location entity = locationMapper.toEntity(locationDto);
        Location saved = locationRepository.save(entity);
        return locationMapper.toDto(saved);
    }

    @Override
    public LocationDto getLocationById(Long locationId) {
        Location location = locationRepository.findById(locationId)
                .orElseThrow(() -> new NotFoundException("Location with id=" + locationId + " not found"));
        return locationMapper.toDto(location);
    }

    @Override
    public Page<LocationDto> getAllLocations(Pageable pageable) {
        Page<Location> page = locationRepository.findAll(pageable);
        return page.map(locationMapper::toDto);
    }

    @Override
    public List<LocationDto> getAllLocations() {
        List<Location> locations = locationRepository.findAll();

        return locations.stream().map(locationMapper::toDto).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public LocationDto updateLocation(Long locationId, LocationDto locationDto) {
        Location existing = locationRepository.findById(locationId)
                .orElseThrow(() -> new NotFoundException("Location with id=" + locationId + " not found"));
        locationMapper.partialUpdate(locationDto, existing);
        Location updated = locationRepository.save(existing);
        return locationMapper.toDto(updated);
    }

    @Override
    @Transactional
    public void deleteLocation(Long locationId) {
        if (!locationRepository.existsById(locationId)) {
            throw new NotFoundException("Location with id=" + locationId + " not found");
        }
        locationRepository.deleteById(locationId);
    }
}
