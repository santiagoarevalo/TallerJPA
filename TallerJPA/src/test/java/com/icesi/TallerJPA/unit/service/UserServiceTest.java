package com.icesi.TallerJPA.unit.service;

import com.icesi.TallerJPA.dto.request.IcesiUserDTO;
import com.icesi.TallerJPA.mapper.UserMapper;
import com.icesi.TallerJPA.mapper.UserMapperImpl;
import com.icesi.TallerJPA.model.IcesiRole;
import com.icesi.TallerJPA.model.IcesiUser;
import com.icesi.TallerJPA.repository.RoleRepository;
import com.icesi.TallerJPA.repository.UserRespository;
import com.icesi.TallerJPA.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UserServiceTest {

    private UserMapper userMapper;

    private UserRespository userRespository;

    private RoleRepository roleRepository;

    private UserService userService;

    @BeforeEach
    public void setup() {
        userMapper = spy(UserMapperImpl.class);
        userRespository = mock(UserRespository.class);
        roleRepository = mock(RoleRepository.class);

        userService = new UserService(userMapper, userRespository, roleRepository);
    }

    private IcesiUserDTO defaultUserDTO() {
        return IcesiUserDTO.builder()
                .firstName("Juan")
                .lastName("Perez")
                .email("test@example.com")
                .phoneNumber("123456789")
                .password("123456")
                .rolName("USER")
                .build();
    }

    private IcesiUser defaultUser() {
        return IcesiUser.builder()
                .firstName("Juan")
                .lastName("Perez")
                .email("test@example.com")
                .phoneNumber("123456789")
                .password("123456")
                .icesiRole(defaultRole())
                .build();

    }

    private IcesiRole defaultRole() {
        return IcesiRole.builder()
                .name("USER")
                .description("User")
                .build();
    }


    @Test
    public void testSaveUser() {

        when(roleRepository.findIcesiRoleByName(any())).thenReturn(Optional.of(defaultRole()));
        when(userRespository.existsByEmail(anyString())).thenReturn(false);
        when(userRespository.existsByPhoneNumber(anyString())).thenReturn(false);
        when(userMapper.fromIcesiUser(any(IcesiUserDTO.class))).thenReturn(defaultUser());

        userService.save(defaultUserDTO());

        verify(roleRepository, times(1)).findIcesiRoleByName(anyString());
        verify(userRespository, times(1)).existsByEmail(anyString());
        verify(userRespository, times(1)).existsByPhoneNumber(anyString());
        verify(userMapper, times(1)).fromIcesiUser(any(IcesiUserDTO.class));
        verify(userRespository, times(1)).save(any(IcesiUser.class));


    }

    @Test
    public void testSaveUserWhenEmailAlreadyExists() {
        when(userRespository.existsByEmail(any())).thenReturn(true);
        try {
            userService.save(defaultUserDTO());
            fail();
        } catch (RuntimeException exception) {
            String message = exception.getMessage();
            assertEquals("Email is repeated", message);
        }
    }

    @Test
    public void testSaveUserWhenPhoneNumberAlreadyExists() {
        when(userRespository.existsByPhoneNumber(any())).thenReturn(true);
        try {
            userService.save(defaultUserDTO());
            fail();
        } catch (RuntimeException exception) {
            String message = exception.getMessage();
            assertEquals("Phone is repeated", message);
        }
    }

    @Test
    public void testSaveUserWhenEmailAndPhoneNumberAlreadyExists() {
        when(userRespository.existsByEmail(any())).thenReturn(true);
        when(userRespository.existsByPhoneNumber(any())).thenReturn(true);
        try {
            userService.save(defaultUserDTO());
            fail();
        } catch (RuntimeException exception) {
            String message = exception.getMessage();
            assertEquals("Email and phone are repeated", message);
        }
    }

    @Test
    public void testSaveUserWhenRoleDoesNotExist() {
        when(roleRepository.findIcesiRoleByName(any())).thenReturn(Optional.empty());
        try {
            userService.save(defaultUserDTO());
            fail();
        } catch (RuntimeException exception) {
            String message = exception.getMessage();
            assertEquals("Role not found", message);
        }
    }


}
