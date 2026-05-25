package personal_finance_manager.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import personal_finance_manager.dto.CreateGoalRequest;
import personal_finance_manager.entity.Goal;
import personal_finance_manager.repository.GoalRepository;

import java.util.List;

@Service
@RequiredArgsConstructor

public class GoalService {

    private final GoalRepository goalRepository;


    public String create(
            CreateGoalRequest request
    ) {

        Goal goal =
                Goal.builder()

                        .goalName(
                                request.getGoalName()
                        )

                        .targetAmount(
                                request.getTargetAmount()
                        )

                        .targetDate(
                                request.getTargetDate()
                        )

                        .build();

        goalRepository.save(
                goal
        );

        return "Goal created";

    }


    public List<Goal>
    getAll() {

        return goalRepository.findAll();

    }

}