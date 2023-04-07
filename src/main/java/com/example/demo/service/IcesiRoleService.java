package com.example.demo.service;

import java.util.Optional;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.example.demo.DTO.IcesiRoleCreateDTO;
import com.example.demo.mapper.IcesiRoleMapper;
import com.example.demo.model.IcesiRole;
import com.example.demo.repository.IcesiRoleRepository;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class IcesiRoleService {
    
    private final IcesiRoleRepository icesiRoleRepository;
    private final IcesiRoleMapper icesiRoleMapper;

    public IcesiRoleCreateDTO create(IcesiRoleCreateDTO role) {

        Optional<IcesiRole> existingIcesiRole = icesiRoleRepository.findByName(role.getName());

        existingIcesiRole.ifPresent(u -> {throw new RuntimeException("This role name is already in use");});

        IcesiRole newIcesiRole = icesiRoleMapper.fromIcesiRoleCreateDTO(role);
        newIcesiRole.setRoleId(UUID.randomUUID());
        
        return icesiRoleMapper.fromIcesiRole(icesiRoleRepository.save(newIcesiRole));
    }
}