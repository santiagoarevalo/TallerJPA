package com.edu.icesi.TallerJPA.api;

import com.edu.icesi.TallerJPA.dto.UserCreateDTO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

public interface UserAPI {

    String BASE_USER_URL = "/users";

    @GetMapping("/{email}/")
    UserCreateDTO getUser(@PathVariable String email);

    List<UserCreateDTO> getAllUsers();

    UserCreateDTO addUser(@RequestBody UserCreateDTO userCreateDTO);

    @GetMapping("/{phoneNumber}/")
    UserCreateDTO getUserByPhoneNumber(@PathVariable String phoneNumber);
}
