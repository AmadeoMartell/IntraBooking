package com.epam.capstone.service.impl;

import com.epam.capstone.dto.StatusDto;
import com.epam.capstone.dto.mapper.StatusMapper;
import com.epam.capstone.exception.NotFoundException;
import com.epam.capstone.model.Status;
import com.epam.capstone.repository.StatusRepository;
import com.epam.capstone.service.StatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of {@link StatusService} that manages statuses via StatusRepository and maps entities to DTOs.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class StatusServiceImpl implements StatusService {

    private final StatusRepository statusRepository;
    private final StatusMapper statusMapper;

    @Override
    @Transactional
    public StatusDto createStatus(StatusDto statusDto) {
        Status entity = statusMapper.toEntity(statusDto);
        Status saved = statusRepository.save(entity);
        return statusMapper.toDto(saved);
    }

    @Override
    public StatusDto getStatusById(Short statusId) {
        Status status = statusRepository.findById(statusId)
                .orElseThrow(() -> new NotFoundException("Status with id=" + statusId + " not found"));
        return statusMapper.toDto(status);
    }

    @Override
    public Page<StatusDto> getAllStatuses(Pageable pageable) {
        Page<Status> page = statusRepository.findAll(pageable);
        return page.map(statusMapper::toDto);
    }

    @Override
    public List<StatusDto> findAll() {
        return statusRepository.findAll().stream().map(statusMapper::toDto).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public StatusDto updateStatus(Short statusId, StatusDto statusDto) {
        Status existing = statusRepository.findById(statusId)
                .orElseThrow(() -> new NotFoundException("Status with id=" + statusId + " not found"));
        statusMapper.partialUpdate(statusDto, existing);
        Status updated = statusRepository.save(existing);
        return statusMapper.toDto(updated);
    }

    @Override
    @Transactional
    public void deleteStatus(Short statusId) {
        if (!statusRepository.existsById(statusId)) {
            throw new NotFoundException("Status with id=" + statusId + " not found");
        }
        statusRepository.deleteById(statusId);
    }

    @Override
    public StatusDto getStatusByName(String name) {
        Status existing = statusRepository.findByName(name)
                .orElseThrow(() -> new NotFoundException("Status with name=" + name + " not found"));
        return statusMapper.toDto(existing);
    }
}
