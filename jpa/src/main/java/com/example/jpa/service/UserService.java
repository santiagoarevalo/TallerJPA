package com.example.jpa.service;

import com.example.jpa.dto.UserRequestDTO;
import com.example.jpa.dto.UserResponseDTO;
import com.example.jpa.exceptions.NotFoundRoleException;
import com.example.jpa.exceptions.UserNotFoundException;
import com.example.jpa.mapper.UserMapper;
import com.example.jpa.model.IcesiRole;
import com.example.jpa.model.IcesiUser;
import com.example.jpa.repository.RoleRepository;
import com.example.jpa.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final RoleRepository roleRepository;

    public UserResponseDTO save(UserRequestDTO user) {
        validateEmailAndPhoneNumberExists(user);
        IcesiUser icesiUser = userMapper.fromUserRequestDTO(user);
        icesiUser.setUserId(UUID.randomUUID()); //User ID is generated by the service
        icesiUser.setIcesiAccountList(new ArrayList<>());
        IcesiRole icesiRole = validateRoleExists(user); //Validate if the role exists
        icesiUser.setRole(icesiRole);
        userRepository.save(icesiUser);
        roleRepository.save(icesiRole);
        return userMapper.fromUserToUserResponseDTO(icesiUser);
    }

    public UserResponseDTO getUser(String email) {
        if(userRepository.findByEmail(email).isPresent()) {
            return userMapper.fromUserToUserResponseDTO(userRepository.findByEmail(email).get());
        }else {
            throw new UserNotFoundException("User not found");
        }
    }

    public List<UserResponseDTO> getUsers(){
        return userRepository.findAll().stream().map(userMapper::fromUserToUserResponseDTO).collect(Collectors.toList());
    }

    //Validate if exists a user with the same email and phone number
    private void validateEmailAndPhoneNumberExists(UserRequestDTO user) {
        if(userRepository.findByEmail(user.getEmail()).isPresent()
                && userRepository.findByPhoneNumber(user.getPhoneNumber()).isPresent()) {
            throw new RuntimeException("Already exists an user with the same email and phone number");
        }else if(userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new RuntimeException("Already exists an user with the same email");
        }else if(userRepository.findByPhoneNumber(user.getPhoneNumber()).isPresent()) {
            throw new RuntimeException("Already exists an user with the same phone number");
        }
    }

    //Validate if the new user's role exists
    private IcesiRole validateRoleExists(UserRequestDTO user) {
        if(user.getRole() != null) {
            return roleRepository.getByName(user.getRole().getName()).
                    orElseThrow(() -> new NotFoundRoleException("Role " + user.getRole().getName() + " not found or is null"));
        }else {
            throw new RuntimeException("Role is null");
        }
    }

}