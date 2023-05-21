package com.edu.icesi.demojpa.unit.service.Test;

import com.edu.icesi.demojpa.dto.request.RoleDTO;
import com.edu.icesi.demojpa.error.exception.IcesiException;
import com.edu.icesi.demojpa.error.util.IcesiExceptionBuilder;
import com.edu.icesi.demojpa.mapper.RoleMapper;
import com.edu.icesi.demojpa.mapper.RoleMapperImpl;
import com.edu.icesi.demojpa.model.IcesiRole;
import com.edu.icesi.demojpa.repository.RoleRepository;
import com.edu.icesi.demojpa.service.RoleService;
import com.edu.icesi.demojpa.unit.service.Matcher.IcesiRoleMatcher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class RoleServiceTest {
    private RoleService roleService;
    private RoleRepository roleRepository;
    private RoleMapper roleMapper;

    @BeforeEach
    private void init(){
        roleRepository = mock(RoleRepository.class);
        roleMapper = spy(RoleMapperImpl.class);
        roleService = new RoleService(roleRepository, roleMapper);
    }

    @Test
    public void testCreateRole(){
        roleService.save(defaultRoleDTO());
        IcesiRole icesiRoleToCompare = defaultIcesiRole();
        verify(roleMapper, times(1)).fromIcesiRoleDTO(any());
        verify(roleRepository, times(1)).save(argThat(new IcesiRoleMatcher(icesiRoleToCompare)));
    }

    @Test
    public void testCreateRoleWhenItExists(){
        when(roleRepository.findRoleByName(any())).thenReturn(Optional.ofNullable(defaultIcesiRole()));
        try{
            roleService.save(defaultRoleDTO());
        }catch (IcesiException exception){
            String message = exception.getMessage();
            assertEquals("This role is already in use", message);
        }
    }

    private IcesiRole defaultIcesiRole(){
        return IcesiRole.builder()
                .name("Student")
                .description("Loreno Insomnio, nunca supe como se dice")
                .build();
    }

    private RoleDTO defaultRoleDTO(){
        return RoleDTO.builder()
                .name("Student")
                .description("Loreno Insomnio, nunca supe como se dice")
                .build();
    }
}
