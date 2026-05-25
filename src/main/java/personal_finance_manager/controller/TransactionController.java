package personal_finance_manager.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import personal_finance_manager.dto.CreateTransactionRequest;
import personal_finance_manager.dto.UpdateTransactionRequest;
import personal_finance_manager.service.TransactionService;

import java.util.Map;

@RestController
@RequestMapping("/api/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;

    /**
     * Create a new transaction.
     * POST /api/transactions
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> create(@Valid @RequestBody CreateTransactionRequest request) {
        Map<String, Object> response = transactionService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Get all transactions with optional filters.
     * GET /api/transactions?startDate=...&endDate=...&categoryId=...
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAll(
            @RequestParam(required = false) String startDate,
            @RequestParam(required = false) String endDate,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Long categoryId) {
        Map<String, Object> response = transactionService.getAll(startDate, endDate, category, categoryId);
        return ResponseEntity.ok(response);
    }

    /**
     * Update a transaction.
     * PUT /api/transactions/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> update(@PathVariable Long id,
                                                      @Valid @RequestBody UpdateTransactionRequest request) {
        Map<String, Object> response = transactionService.update(id, request);
        return ResponseEntity.ok(response);
    }

    /**
     * Delete a transaction.
     * DELETE /api/transactions/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> delete(@PathVariable Long id) {
        Map<String, Object> response = transactionService.delete(id);
        return ResponseEntity.ok(response);
    }
}