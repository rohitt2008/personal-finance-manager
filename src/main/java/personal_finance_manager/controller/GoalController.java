package personal_finance_manager.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import personal_finance_manager.dto.CreateGoalRequest;
import personal_finance_manager.entity.Goal;
import personal_finance_manager.service.GoalService;

import java.util.List;

@RestController
@RequestMapping(
        "/api/goals"
)

@RequiredArgsConstructor

public class GoalController {

    private final
    GoalService goalService;


    @PostMapping

    public String create(

            @RequestBody
            CreateGoalRequest request

    ) {

        return goalService
                .create(
                        request
                );

    }


    @GetMapping

    public List<Goal>
    getAll() {

        return goalService
                .getAll();

    }

}