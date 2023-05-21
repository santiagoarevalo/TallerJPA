package com.edu.icesi.TallerJPA.service;

import com.edu.icesi.TallerJPA.dto.RoleCreateDTO;
import com.edu.icesi.TallerJPA.dto.UserCreateDTO;
import com.edu.icesi.TallerJPA.error.exception.DetailBuilder;
import com.edu.icesi.TallerJPA.error.exception.ErrorCode;
import com.edu.icesi.TallerJPA.mapper.UserMapper;
import com.edu.icesi.TallerJPA.model.IcesiRole;
import com.edu.icesi.TallerJPA.model.IcesiUser;
import com.edu.icesi.TallerJPA.repository.RoleRepository;
import com.edu.icesi.TallerJPA.repository.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

import static com.edu.icesi.TallerJPA.error.util.IcesiExceptionBuilder.createIcesiException;

@Service
@AllArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    private final UserMapper userMapper;

    private final RoleRepository roleRepository;

    public UserCreateDTO save(UserCreateDTO userCreateDTO){

        verifyEmailAndPhoneNumber(userCreateDTO.getEmail(), userCreateDTO.getPhoneNumber());

        findByEmailIfIsDuplicated(userCreateDTO.getEmail());
        findByPhoneNumberIfIsDuplicated(userCreateDTO.getPhoneNumber());

        String roleName = userCreateDTO.getIcesiRole().getName();
        roleRepository.findByName(roleName).orElseThrow(() -> new RuntimeException("The role "+roleName+" not exists"));

        IcesiUser icesiUser = userMapper.fromIcesiUserDTO(userCreateDTO);
        icesiUser.setUserId(UUID.randomUUID());
        icesiUser.setIcesiRole(userCreateDTO.getIcesiRole());

        return userMapper.fromIcesiUser(userRepository.save(icesiUser));
    }

    private void verifyEmailAndPhoneNumber(String email, String phoneNumber){

        if (userRepository.findByEmail(email).isPresent() && userRepository.findByPhoneNumber(phoneNumber).isPresent()){
            throw createIcesiException(
                    "Invalid values",
                    HttpStatus.BAD_REQUEST,
                    new DetailBuilder(ErrorCode.ERR_400, "The email and phone number","save a user", "The email and phone number already exists")
            ).get();
        }
    }

    public void findByEmailIfIsDuplicated(String email){

        Optional<IcesiUser> user = userRepository.findByEmail(email);

        if (user.isPresent()) {
            throw createIcesiException(
                    "Duplicated email",
                    HttpStatus.CONFLICT,
                    new DetailBuilder(ErrorCode.ERR_DUPLICATED, "user", "email", email)
            ).get();
        }
    }

    public void findByPhoneNumberIfIsDuplicated(String phoneNumber){

        Optional<IcesiUser> user = userRepository.findByPhoneNumber(phoneNumber);

        if (user.isPresent()) {
            throw createIcesiException(
                    "Duplicated phoneNumber",
                    HttpStatus.CONFLICT,
                    new DetailBuilder(ErrorCode.ERR_DUPLICATED, "user", "phone number", phoneNumber)
            ).get();
        }
    }

    public UserCreateDTO findByEmail(String email){
        return userMapper.fromIcesiUser(userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("The user with email "+email+" not exists")));
    }

    public UserCreateDTO findByPhoneNumber(String phoneNumber){
        return userMapper.fromIcesiUser(userRepository.findByEmail(phoneNumber).orElseThrow(() -> new RuntimeException("The user with phone number "+phoneNumber+" not exists")));
    }

}
