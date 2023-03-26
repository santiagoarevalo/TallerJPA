package co.edu.icesi.demo.unit.service;

import co.edu.icesi.demo.dto.UserCreateDTO;
import co.edu.icesi.demo.mapper.UserMapper;
import co.edu.icesi.demo.mapper.UserMapperImpl;
import co.edu.icesi.demo.model.IcesiRole;
import co.edu.icesi.demo.model.IcesiUser;
import co.edu.icesi.demo.repository.RoleRepository;
import co.edu.icesi.demo.repository.UserRepository;
import co.edu.icesi.demo.service.UserService;
import co.edu.icesi.demo.unit.service.matcher.IcesiUserMatcher;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.*;

public class UserServiceTest {

    private UserService userService;

    private UserMapper userMapper;
    private UserRepository userRepository;
    private RoleRepository roleRepository;


    @BeforeEach
    private void init(){
        userRepository=mock(UserRepository.class);
        userMapper=spy(UserMapperImpl.class);
        roleRepository=mock(RoleRepository.class);

        userService=new UserService(userRepository, userMapper, roleRepository);
    }


    @Test
    public void testCreateUser() {
        when(roleRepository.findByName(any())).thenReturn(Optional.of(defaultIcesiRole()));
        when(userRepository.findByEmail(any())).thenReturn(Optional.empty());
        when(userRepository.findByPhoneNumber(any())).thenReturn(Optional.empty());

        userService.save(defaultUserCreateDTO());
        IcesiUser icesiUser = defaultIcesiUser();

       verify(userMapper,times(1)).fromIcesiUserDTO(any());
        verify(userRepository, times(1)).save(argThat(new IcesiUserMatcher(icesiUser)));
        verify(userMapper,times(1)).fromIcesiUser(any());

    }

   @Test
    public void testCreateUserWhenEmailAlreadyExists(){
       when(userRepository.findByEmail(any())).thenReturn(Optional.of(defaultIcesiUser()));

        try{
            userService.save(defaultUserCreateDTO());
            fail();
        }catch (RuntimeException exception){
            String message= exception.getMessage();
            assertEquals("User email is in use",message);

            verify(userRepository,times(2)).findByEmail(any());
            verify(roleRepository,never()).findByName(any());
            verify(userMapper,never()).fromIcesiUserDTO(any());
            verify(userRepository, never()).save(any());
            verify(userMapper,never()).fromIcesiUser(any());

        }
    }

    @Test
    public void testCreateUserWhenPhoneNumberAlreadyExists(){

        when(userRepository.findByEmail(any())).thenReturn(Optional.empty());
        when(userRepository.findByPhoneNumber(any())).thenReturn(Optional.of(defaultIcesiUser()));
        try{
            userService.save(defaultUserCreateDTO());
            fail();
        }catch (RuntimeException exception){
            String message= exception.getMessage();
            assertEquals("User phone number is in use",message);

           verify(userRepository,times(1)).findByPhoneNumber(any());
            verify(roleRepository,never()).findByName(any());
            verify(userMapper,never()).fromIcesiUserDTO(any());
            verify(userRepository, never()).save(any());
            verify(userMapper,never()).fromIcesiUser(any());
        }
    }

    @Test
    public void testCreateUserWhenEmailAndPhoneNumberAlreadyExists(){
        when(userRepository.findByEmail(any())).thenReturn(Optional.of(defaultIcesiUser()));
        when(userRepository.findByPhoneNumber(any())).thenReturn(Optional.of(defaultIcesiUser()));

        try{
            userService.save(defaultUserCreateDTO());
            fail();
        }catch (RuntimeException exception){
            String message= exception.getMessage();
            assertEquals("User email and phone number are in use",message);

            verify(userRepository,times(1)).findByEmail(any());
            verify(userRepository,times(1)).findByPhoneNumber(any());
            verify(roleRepository,never()).findByName(any());
            verify(userMapper,never()).fromIcesiUserDTO(any());
            verify(userRepository, never()).save(any());
            verify(userMapper,never()).fromIcesiUser(any());
        }
    }

    @Test
    public void testCreateUserWhenRoleDoesNotExists(){

        when(userRepository.findByEmail(any())).thenReturn(Optional.empty());
        when(userRepository.findByPhoneNumber(any())).thenReturn(Optional.empty());

        try {
            userService.save(defaultUserCreateDTO());
            fail();
        }catch (RuntimeException exception){
            String message= exception.getMessage();
            assertEquals("User role does not exists",message);

            verify(userRepository,times(2)).findByEmail(any());
            verify(userRepository,times(1)).findByPhoneNumber(any());
            verify(roleRepository,times(1)).findByName(any());
            verify(userMapper,never()).fromIcesiUserDTO(any());
            verify(userRepository, never()).save(any());
            verify(userMapper,never()).fromIcesiUser(any());
        }
    }

    private UserCreateDTO defaultUserCreateDTO(){

        return UserCreateDTO.builder()
                .firstName("Julieta")
                .lastName("Venegas")
                .email("julietav@example.com")
                .phoneNumber("3184441232")
                .password("julieta123")
                .roleName("administrator")
                .build();
    }

    private IcesiUser defaultIcesiUser(){

        return IcesiUser.builder()
                .firstName("Julieta")
                .lastName("Venegas")
                .email("julietav@example.com")
                .phoneNumber("3184441232")
                .password("julieta123")
                .role(defaultIcesiRole())
                .userId(UUID.randomUUID())
                .build();
    }

    private IcesiRole defaultIcesiRole(){
        return IcesiRole.builder()
                .name("administrator")
                .description("is an administrator")
                .roleId(UUID.randomUUID())
                .build();
    }

}
