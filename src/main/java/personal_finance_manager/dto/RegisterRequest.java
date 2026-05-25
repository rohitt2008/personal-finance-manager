package personal_finance_manager.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class RegisterRequest {


    @Email(
            message =
                    "Invalid email"
    )

    @NotBlank(
            message =
                    "Username required"
    )

    private String username;



    @NotBlank(
            message =
                    "Password required"
    )

    private String password;



    @NotBlank(
            message =
                    "Full name required"
    )

    private String fullName;



    @NotBlank(
            message =
                    "Phone required"
    )

    private String phoneNumber;

}