package co.edu.icesi.tallerjpa.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TransactionCreateDTO {
    private String senderAccountNumber;
    private String receiverAccountNumber;
    private long amount;
}
