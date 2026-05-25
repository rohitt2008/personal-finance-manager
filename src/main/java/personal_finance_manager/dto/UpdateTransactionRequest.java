package personal_finance_manager.dto;

import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class UpdateTransactionRequest {


    @Positive(
            message =
                    "Amount must be positive"
    )

    private Double amount;


    private String description;


    private String category;

}