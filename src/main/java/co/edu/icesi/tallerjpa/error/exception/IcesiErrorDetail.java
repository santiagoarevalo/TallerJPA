package co.edu.icesi.tallerjpa.error.exception;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Builder
@Data
@AllArgsConstructor
public class IcesiErrorDetail {
    private String errorCode;
    private String errorMessage;
}
