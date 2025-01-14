package com.example.jpa.service;

import com.example.jpa.dto.UserDTO;
import com.example.jpa.error.exceptions.RoleException;
import com.example.jpa.error.exceptions.UserException;
import com.example.jpa.error.util.IcesiError;
import com.example.jpa.error.util.IcesiErrorCode;
import com.example.jpa.error.util.IcesiErrorDetail;
import com.example.jpa.mapper.UserMapper;
import com.example.jpa.model.IcesiRole;
import com.example.jpa.model.IcesiUser;
import com.example.jpa.repository.RoleRepository;
import com.example.jpa.repository.UserRepository;
import com.example.jpa.security.IcesiSecurityContext;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.validation.ValidationException;
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

    public UserDTO save(UserDTO user) {
        validateFirstAndLastNameNotBlank(user);
        validateEmailAndPhoneNumberExists(user);
        validateRoleAdminOrBankUser(user.getRole().getName());
        IcesiUser icesiUser = userMapper.fromUserRequestDTO(user);
        icesiUser.setUserId(UUID.randomUUID()); //User ID is generated by the service
        icesiUser.setIcesiAccountList(new ArrayList<>());
        IcesiRole icesiRole = validateRoleExists(user); //Validate if the role exists
        icesiUser.setRole(icesiRole);
        userRepository.save(icesiUser);
        roleRepository.save(icesiRole);
        return userMapper.fromUserToUserResponseDTO(icesiUser);
    }

    public UserDTO getUser(String email) {
        if(userRepository.findByEmail(email).isPresent()) {
            return userMapper.fromUserToUserResponseDTO(userRepository.findByEmail(email).get());
        }else {
            throw new UserException(IcesiError.builder()
                    .status(HttpStatus.BAD_REQUEST)
                    .details(List.of(IcesiErrorDetail.builder()
                            .errorCode(IcesiErrorCode.ERR_404)
                            .errorMessage("User not found")
                            .build()))
                    .build());
        }
    }

    public List<UserDTO> getUsers(){
        return userRepository.findAll().stream().map(userMapper::fromUserToUserResponseDTO).collect(Collectors.toList());
    }

    private void validateFirstAndLastNameNotBlank(UserDTO user) {
        if(user.getFirstName().isBlank() || user.getLastName().isBlank()) {
            throw new ValidationException(IcesiError.builder()
                    .status(HttpStatus.BAD_REQUEST)
                    .details(List.of(IcesiErrorDetail.builder()
                            .errorCode(IcesiErrorCode.ERR_400)
                            .errorMessage("First name and last name must not be blank")
                            .build()))
                    .build().toString());
        }
    }

    //Validate if exists a user with the same email and phone number
    private void validateEmailAndPhoneNumberExists(UserDTO user) {
        if(userRepository.findByEmail(user.getEmail()).isPresent()
                && userRepository.findByPhoneNumber(user.getPhoneNumber()).isPresent()) {
            throw new UserException(IcesiError.builder()
                    .status(HttpStatus.BAD_REQUEST)
                    .details(List.of(IcesiErrorDetail.builder()
                            .errorCode(IcesiErrorCode.ERR_400)
                            .errorMessage("Already exists an user with the same email and phone number")
                            .build()))
                    .build());
        }else if(userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new UserException(IcesiError.builder()
                    .status(HttpStatus.BAD_REQUEST)
                    .details(List.of(IcesiErrorDetail.builder()
                            .errorCode(IcesiErrorCode.ERR_400)
                            .errorMessage("Already exists an user with the same email")
                            .build()))
                    .build());
        }else if(userRepository.findByPhoneNumber(user.getPhoneNumber()).isPresent()) {
            throw new UserException(IcesiError.builder()
                    .status(HttpStatus.BAD_REQUEST)
                    .details(List.of(IcesiErrorDetail.builder()
                            .errorCode(IcesiErrorCode.ERR_400)
                            .errorMessage("Already exists an user with the same phone number")
                            .build()))
                    .build());
        }
    }

    //Validate if the new user's role exists
    private IcesiRole validateRoleExists(UserDTO user) {
        if(user.getRole() != null) {
            return roleRepository.getByName(user.getRole().getName()).
                    orElseThrow(() -> new RoleException(IcesiError.builder()
                            .status(HttpStatus.BAD_REQUEST)
                            .details(List.of(IcesiErrorDetail.builder()
                                    .errorCode(IcesiErrorCode.ERR_400)
                                    .errorMessage("Role " + user.getRole().getName() + " not found")
                                    .build()))
                            .build()));
        }else {
            throw new ValidationException(IcesiError.builder()
                    .status(HttpStatus.BAD_REQUEST)
                    .details(List.of(IcesiErrorDetail.builder()
                            .errorCode(IcesiErrorCode.ERR_400)
                            .errorMessage("Role is required")
                            .build()))
                    .build().toString());
        }
    }

    //Validate if the user that is creating a new user has the role ADMIN or BANK_USER
    private void validateRoleAdminOrBankUser(String newRole) {
        var role = IcesiSecurityContext.getCurrentUserRole();
        if (role.equalsIgnoreCase("USER")) {
            throw new UserException(IcesiError.builder()
                    .status(HttpStatus.BAD_REQUEST)
                    .details(List.of(IcesiErrorDetail.builder()
                            .errorCode(IcesiErrorCode.ERR_403)
                            .errorMessage("You don't have the permissions to create an user")
                            .build()))
                    .build());
        }
        if(role.equalsIgnoreCase("BANK_USER") && newRole.equalsIgnoreCase("ADMIN")) {
            throw new UserException(IcesiError.builder()
                    .status(HttpStatus.BAD_REQUEST)
                    .details(List.of(IcesiErrorDetail.builder()
                            .errorCode(IcesiErrorCode.ERR_403)
                            .errorMessage("You don't have the permissions to create an admin user")
                            .build()))
                    .build());
        }
    }

}
