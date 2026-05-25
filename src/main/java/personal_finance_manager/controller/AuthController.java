package personal_finance_manager.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import personal_finance_manager.dto.RegisterRequest;
import personal_finance_manager.dto.LoginRequest;
import personal_finance_manager.service.AuthService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor

public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public String register(
            @Valid
            @RequestBody
            RegisterRequest request
    ) {

        return authService.register(
                request
        );

    }
    @PostMapping("/login")

    public String login(

            @RequestBody
            LoginRequest request

    ) {

        return authService.login(
                request
        );

    }

}