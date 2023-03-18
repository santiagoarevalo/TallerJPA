package com.example.jpa.service;

import com.example.jpa.dto.RoleDTO;
import com.example.jpa.mapper.RoleMapper;
import com.example.jpa.model.IcesiRole;
import com.example.jpa.repository.RoleRepository;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class RoleService {

    private final RoleRepository roleRepository;
    private final RoleMapper roleMapper;

    @SneakyThrows
    public IcesiRole save(RoleDTO roleDTO){
        //Role name should be unique
        if (roleRepository.findByName(roleDTO.getName())){
            throw new RuntimeException("Role name already exists");
        }else{
            IcesiRole role = roleMapper.fromRoleDTO(roleDTO);
            role.setRoleId(UUID.randomUUID()); //Role ID is generated by the service
            role.setIcesiUserList(new ArrayList<>());
            return roleRepository.save(role);
        }
    }

    public List<RoleDTO> getRoles(){
        return roleRepository.findAll().stream().map(roleMapper::fromRole).collect(Collectors.toList());
    }
}