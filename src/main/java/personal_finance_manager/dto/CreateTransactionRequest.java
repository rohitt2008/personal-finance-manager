package personal_finance_manager.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class CreateTransactionRequest {

    private Double amount;

    private String date;

    private String description;

    private String category;

}