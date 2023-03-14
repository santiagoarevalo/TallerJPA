package co.edu.icesi.tallerjpa.dto;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class ShowIcesiUserDTO {
    private UUID userId;
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
}
