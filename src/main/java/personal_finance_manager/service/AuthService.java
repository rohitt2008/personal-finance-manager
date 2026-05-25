package personal_finance_manager.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import personal_finance_manager.dto.RegisterRequest;
import personal_finance_manager.dto.LoginRequest;
import personal_finance_manager.entity.User;
import personal_finance_manager.repository.UserRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor

public class AuthService {

    private final UserRepository userRepository;


    public String register(
            RegisterRequest request
    ) {

        User user = User.builder()
                .username(request.getUsername())
                .password(request.getPassword())
                .fullName(request.getFullName())
                .phoneNumber(request.getPhoneNumber())
                .build();

        userRepository.save(user);

        return "User registered successfully";
    }


    public String login(
            LoginRequest request
    ) {

        Optional<User> user =
                userRepository.findByUsername(
                        request.getUsername()
                );

        if (user.isEmpty()) {

            return "Invalid credentials";

        }

        if (!user.get()
                .getPassword()
                .equals(
                        request.getPassword()
                )) {

            return "Invalid credentials";

        }

        return "Login successful";

    }

}