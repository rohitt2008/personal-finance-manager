package personal_finance_manager.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class CreateCategoryRequest {

    private String name;

    private String type;

}