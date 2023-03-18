package com.example.tallerjpa.service;

import com.example.tallerjpa.dto.RoleDTO;
import com.example.tallerjpa.mapper.RoleMapper;
import com.example.tallerjpa.model.IcesiRole;
import com.example.tallerjpa.repository.RoleRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import javax.management.relation.Role;
import java.util.UUID;

@Service
@AllArgsConstructor
public class RoleService {

    private final RoleRepository roleRepository;
    private final RoleMapper roleMapper;

    public IcesiRole saveRole(RoleDTO roleDTO){
        IcesiRole icesiRole = createRole(roleDTO);
        return roleRepository.save(icesiRole);
    }

    public IcesiRole createRole(RoleDTO roleDTO){
        IcesiRole icesiRole = roleMapper.fromRoleDTO(roleDTO);
        if(roleRepository.existsByName(roleDTO.getName())){throw new RuntimeException("Role's name must be unique");}
        icesiRole.setRoleId(UUID.randomUUID());
        return icesiRole;
    }


}
