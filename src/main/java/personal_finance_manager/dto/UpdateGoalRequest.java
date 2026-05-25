package personal_finance_manager.dto;

import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UpdateGoalRequest {

    @Positive(message = "Target amount must be positive")
    private Double targetAmount;

    private String targetDate;

    private String goalName;
}