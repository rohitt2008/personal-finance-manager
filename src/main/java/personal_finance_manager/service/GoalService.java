package personal_finance_manager.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import personal_finance_manager.dto.CreateGoalRequest;
import personal_finance_manager.dto.UpdateGoalRequest;
import personal_finance_manager.entity.Goal;
import personal_finance_manager.entity.Transaction;
import personal_finance_manager.entity.User;
import personal_finance_manager.exception.BadRequestException;
import personal_finance_manager.exception.ForbiddenException;
import personal_finance_manager.exception.ResourceNotFoundException;
import personal_finance_manager.repository.GoalRepository;
import personal_finance_manager.repository.TransactionRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GoalService {

    private final GoalRepository goalRepository;
    private final TransactionRepository transactionRepository;
    private final AuthService authService;

    /**
     * Create a new savings goal.
     */
    public Map<String, Object> create(CreateGoalRequest request) {
        User user = authService.getCurrentUser();

        // Validate target amount
        if (request.getTargetAmount() == null || request.getTargetAmount() <= 0) {
            throw new BadRequestException("Target amount must be positive");
        }

        // Validate target date
        if (request.getTargetDate() == null || request.getTargetDate().isBlank()) {
            throw new BadRequestException("Target date is required");
        }
        try {
            LocalDate targetDate = LocalDate.parse(request.getTargetDate());
            if (!targetDate.isAfter(LocalDate.now())) {
                throw new BadRequestException("Target date must be in the future");
            }
        } catch (DateTimeParseException e) {
            throw new BadRequestException("Invalid target date format");
        }

        // Start date defaults to today
        String startDate = request.getStartDate();
        if (startDate == null || startDate.isBlank()) {
            startDate = LocalDate.now().toString();
        }

        Goal goal = Goal.builder()
                .goalName(request.getGoalName())
                .targetAmount(request.getTargetAmount())
                .targetDate(request.getTargetDate())
                .startDate(startDate)
                .user(user)
                .build();

        goal = goalRepository.save(goal);

        return buildGoalResponse(goal, user);
    }

    /**
     * Get all goals for the current user.
     */
    public Map<String, Object> getAll() {
        User user = authService.getCurrentUser();

        List<Goal> goals = goalRepository.findByUser(user);

        List<Map<String, Object>> goalList = goals.stream()
                .map(g -> buildGoalResponse(g, user))
                .collect(Collectors.toList());

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("goals", goalList);
        return response;
    }

    /**
     * Get a single goal by ID.
     */
    public Map<String, Object> getById(Long id) {
        User user = authService.getCurrentUser();

        Goal goal = goalRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new ResourceNotFoundException("Goal not found"));

        return buildGoalResponse(goal, user);
    }

    /**
     * Update a goal.
     */
    public Map<String, Object> update(Long id, UpdateGoalRequest request) {
        User user = authService.getCurrentUser();

        Goal goal = goalRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new ResourceNotFoundException("Goal not found"));

        if (request.getTargetAmount() != null) {
            if (request.getTargetAmount() <= 0) {
                throw new BadRequestException("Target amount must be positive");
            }
            goal.setTargetAmount(request.getTargetAmount());
        }

        if (request.getTargetDate() != null) {
            goal.setTargetDate(request.getTargetDate());
        }

        if (request.getGoalName() != null) {
            goal.setGoalName(request.getGoalName());
        }

        goal = goalRepository.save(goal);

        return buildGoalResponse(goal, user);
    }

    /**
     * Delete a goal.
     */
    public Map<String, Object> delete(Long id) {
        User user = authService.getCurrentUser();

        Goal goal = goalRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new ResourceNotFoundException("Goal not found"));

        goalRepository.delete(goal);

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("message", "Goal deleted successfully");
        return response;
    }

    /**
     * Build a goal response with dynamically computed progress.
     * Progress = (Total Income - Total Expenses) since goal start date.
     */
    private Map<String, Object> buildGoalResponse(Goal goal, User user) {
        // Calculate progress from transactions since start date
        List<Transaction> transactions = transactionRepository
                .findByUserAndDateGreaterThanEqual(user, goal.getStartDate());

        double totalIncome = transactions.stream()
                .filter(t -> "INCOME".equals(t.getType()))
                .mapToDouble(Transaction::getAmount)
                .sum();

        double totalExpenses = transactions.stream()
                .filter(t -> "EXPENSE".equals(t.getType()))
                .mapToDouble(Transaction::getAmount)
                .sum();

        double currentProgress = totalIncome - totalExpenses;
        if (currentProgress < 0) {
            currentProgress = 0;
        }

        double progressPercentage = 0.0;
        if (goal.getTargetAmount() > 0) {
            progressPercentage = BigDecimal.valueOf(currentProgress)
                    .divide(BigDecimal.valueOf(goal.getTargetAmount()), 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100))
                    .setScale(2, RoundingMode.HALF_UP)
                    .doubleValue();
        }

        double remainingAmount = goal.getTargetAmount() - currentProgress;
        if (remainingAmount < 0) {
            remainingAmount = 0;
        }

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("id", goal.getId());
        response.put("goalName", goal.getGoalName());
        response.put("targetAmount", goal.getTargetAmount());
        response.put("targetDate", goal.getTargetDate());
        response.put("startDate", goal.getStartDate());
        response.put("currentProgress", currentProgress);
        response.put("progressPercentage", progressPercentage);
        response.put("remainingAmount", remainingAmount);
        return response;
    }
}