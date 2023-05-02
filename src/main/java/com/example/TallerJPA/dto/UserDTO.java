package com.example.TallerJPA.dto;

import com.example.TallerJPA.validations.ColombianPhone;
import com.example.TallerJPA.validations.NotNullContact;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
@NotNullContact.List({
    @NotNullContact(
            phone = "phoneNumber",
            email = "email",
            message = "Passwords do not match!"
    )
})
public class UserDTO {
    private String firstName;
    private String lastName;
    private String email;
    @ColombianPhone
    private String phoneNumber;
    private String password;
    private String roleName;

}
