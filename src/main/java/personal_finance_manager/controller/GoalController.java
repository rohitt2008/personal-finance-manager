package personal_finance_manager.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import personal_finance_manager.dto.CreateGoalRequest;
import personal_finance_manager.dto.UpdateGoalRequest;
import personal_finance_manager.service.GoalService;

import java.util.Map;

@RestController
@RequestMapping("/api/goals")
@RequiredArgsConstructor
public class GoalController {

    private final GoalService goalService;

    /**
     * Create a new savings goal.
     * POST /api/goals
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> create(@Valid @RequestBody CreateGoalRequest request) {
        Map<String, Object> response = goalService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Get all goals.
     * GET /api/goals
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAll() {
        Map<String, Object> response = goalService.getAll();
        return ResponseEntity.ok(response);
    }

    /**
     * Get a single goal by ID.
     * GET /api/goals/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getById(@PathVariable Long id) {
        Map<String, Object> response = goalService.getById(id);
        return ResponseEntity.ok(response);
    }

    /**
     * Update a goal.
     * PUT /api/goals/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> update(@PathVariable Long id,
                                                      @Valid @RequestBody UpdateGoalRequest request) {
        Map<String, Object> response = goalService.update(id, request);
        return ResponseEntity.ok(response);
    }

    /**
     * Delete a goal.
     * DELETE /api/goals/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> delete(@PathVariable Long id) {
        Map<String, Object> response = goalService.delete(id);
        return ResponseEntity.ok(response);
    }
}