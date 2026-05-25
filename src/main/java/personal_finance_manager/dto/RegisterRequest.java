package personal_finance_manager.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class RegisterRequest {

    private String username;
    private String password;
    private String fullName;
    private String phoneNumber;

}