package co.edu.icesi.demo.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ResponseUserDto {
    private String firstName;
    private String lastName;
    private String email;
    private String password;
    private String phoneNumber;
    private IcesiRoleDto role;
}