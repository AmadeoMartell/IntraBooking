package com.epam.capstone.service.impl;

import com.epam.capstone.dto.LocationDto;
import com.epam.capstone.dto.mapper.LocationMapper;
import com.epam.capstone.exception.NotFoundException;
import com.epam.capstone.model.Location;
import com.epam.capstone.repository.LocationRepository;
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
class LocationServiceImplTest {

    @Mock
    private LocationRepository locationRepository;

    @Mock
    private LocationMapper locationMapper;

    @InjectMocks
    private LocationServiceImpl service;

    private LocationDto dummyDto;
    private Location dummyEntity;

    @BeforeEach
    void setUp() {
        dummyDto = mock(LocationDto.class);
        dummyEntity = new Location();
    }

    @Test
    void createLocation_success() {
        Location toSave = new Location();
        Location saved = new Location();
        saved.setLocationId(99L);
        LocationDto outDto = mock(LocationDto.class);

        when(locationMapper.toEntity(dummyDto)).thenReturn(toSave);
        when(locationRepository.save(toSave)).thenReturn(saved);
        when(locationMapper.toDto(saved)).thenReturn(outDto);

        LocationDto result = service.createLocation(dummyDto);

        assertSame(outDto, result);
        verify(locationMapper).toEntity(dummyDto);
        verify(locationRepository).save(toSave);
        verify(locationMapper).toDto(saved);
    }

    @Test
    void getLocationById_found() {
        Location found = new Location();
        LocationDto outDto = mock(LocationDto.class);

        when(locationRepository.findById(1L)).thenReturn(Optional.of(found));
        when(locationMapper.toDto(found)).thenReturn(outDto);

        LocationDto result = service.getLocationById(1L);

        assertSame(outDto, result);
    }

    @Test
    void getLocationById_notFound() {
        when(locationRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> service.getLocationById(123L));
    }

    @Test
    void findByName_mapsPage() {
        Pageable pg = PageRequest.of(0, 3);
        Location l1 = new Location(), l2 = new Location();
        Page<Location> page = new PageImpl<>(List.of(l1, l2), pg, 2);
        when(locationRepository.findByNameContaining("foo", pg)).thenReturn(page);
        when(locationMapper.toDto(any(Location.class))).thenReturn(dummyDto);

        Page<LocationDto> result = service.findByName("foo", pg);

        assertEquals(2, result.getTotalElements());
        assertTrue(result.getContent().stream().allMatch(dto -> dto == dummyDto));
        verify(locationRepository).findByNameContaining("foo", pg);
        verify(locationMapper, times(2)).toDto(any(Location.class));
    }

    @Test
    void getAllLocations_listMaps() {
        Location l1 = new Location(), l2 = new Location();
        when(locationRepository.findAll()).thenReturn(List.of(l1, l2));
        when(locationMapper.toDto(any(Location.class))).thenReturn(dummyDto);

        List<LocationDto> result = service.getAllLocations();

        assertEquals(2, result.size());
        assertTrue(result.stream().allMatch(dto -> dto == dummyDto));
        verify(locationRepository).findAll();
        verify(locationMapper, times(2)).toDto(any(Location.class));
    }

    @Test
    void updateLocation_success() {
        LocationDto updateDto = mock(LocationDto.class);
        Location existing = new Location();
        existing.setLocationId(55L);
        LocationDto outDto = mock(LocationDto.class);

        when(locationRepository.findById(55L)).thenReturn(Optional.of(existing));
        // partialUpdate is void by default
        when(locationRepository.save(existing)).thenReturn(existing);
        when(locationMapper.toDto(existing)).thenReturn(outDto);

        LocationDto result = service.updateLocation(55L, updateDto);

        assertSame(outDto, result);
        verify(locationRepository).findById(55L);
        verify(locationMapper).partialUpdate(updateDto, existing);
        verify(locationRepository).save(existing);
        verify(locationMapper).toDto(existing);
    }

    @Test
    void updateLocation_notFound() {
        when(locationRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class,
                () -> service.updateLocation(5L, dummyDto));
    }

    @Test
    void deleteLocation_success() {
        when(locationRepository.existsById(7L)).thenReturn(true);

        service.deleteLocation(7L);

        verify(locationRepository).existsById(7L);
        verify(locationRepository).deleteById(7L);
    }

    @Test
    void deleteLocation_notFound() {
        when(locationRepository.existsById(anyLong())).thenReturn(false);
        assertThrows(NotFoundException.class,
                () -> service.deleteLocation(8L));
    }
}
