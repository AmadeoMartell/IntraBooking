package com.epam.capstone.service.impl;

import com.epam.capstone.dto.RoleDto;
import com.epam.capstone.dto.mapper.RoleMapper;
import com.epam.capstone.exception.NotFoundException;
import com.epam.capstone.model.Role;
import com.epam.capstone.repository.RoleRepository;
import com.epam.capstone.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Implementation of {@link RoleService} that manages roles via RoleRepository and maps entities to DTOs.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;
    private final RoleMapper roleMapper;

    @Override
    @Transactional
    public RoleDto createRole(RoleDto roleDto) {
        Role entity = roleMapper.toEntity(roleDto);
        Role saved = roleRepository.save(entity);
        return roleMapper.toDto(saved);
    }

    @Override
    public RoleDto getRoleById(Long roleId) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new NotFoundException("Role with id=" + roleId + " not found"));
        return roleMapper.toDto(role);
    }

    @Override
    public Page<RoleDto> getAllRoles(Pageable pageable) {
        Page<Role> page = roleRepository.findAll(pageable);
        return page.map(roleMapper::toDto);
    }

    @Override
    public List<RoleDto> findAll() {
        var roles = roleRepository.findAll();
        return roles.stream()
                .map(roleMapper::toDto)
                .toList();
    }

    @Override
    @Transactional
    public RoleDto updateRole(Long roleId, RoleDto roleDto) {
        Role existing = roleRepository.findById(roleId)
                .orElseThrow(() -> new NotFoundException("Role with id=" + roleId + " not found"));
        roleMapper.partialUpdate(roleDto, existing);
        Role updated = roleRepository.save(existing);
        return roleMapper.toDto(updated);
    }

    @Override
    @Transactional
    public void deleteRole(Long roleId) {
        if (!roleRepository.existsById(roleId)) {
            throw new NotFoundException("Role with id=" + roleId + " not found");
        }
        roleRepository.deleteById(roleId);
    }
}
