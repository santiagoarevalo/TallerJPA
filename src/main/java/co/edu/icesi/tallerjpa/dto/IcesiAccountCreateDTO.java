package co.edu.icesi.tallerjpa.dto;

import co.edu.icesi.tallerjpa.enums.TypeIcesiAccount;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.UUID;

@Data
@Builder
public class IcesiAccountCreateDTO {
    @Min(value = 0, message = "The min value for the balance is 0")
    private long balance;
    @NotNull(message = "The icesi account type can not be null")
    private TypeIcesiAccount type;
    private boolean active;
    @NotNull(message = "The icesi user can not be null")
    private IcesiUserCreateDTO icesiUserDTO;
}
