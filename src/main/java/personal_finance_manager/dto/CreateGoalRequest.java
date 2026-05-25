package personal_finance_manager.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class CreateGoalRequest {

    private String goalName;

    private Double targetAmount;

    private String targetDate;

}