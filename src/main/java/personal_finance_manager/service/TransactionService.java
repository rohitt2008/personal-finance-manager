package personal_finance_manager.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import personal_finance_manager.dto.CreateTransactionRequest;
import personal_finance_manager.dto.UpdateTransactionRequest;
import personal_finance_manager.entity.Category;
import personal_finance_manager.entity.Transaction;
import personal_finance_manager.entity.User;
import personal_finance_manager.exception.BadRequestException;
import personal_finance_manager.exception.ResourceNotFoundException;
import personal_finance_manager.repository.CategoryRepository;
import personal_finance_manager.repository.TransactionRepository;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final CategoryRepository categoryRepository;
    private final AuthService authService;

    /**
     * Create a new transaction.
     */
    public Map<String, Object> create(CreateTransactionRequest request) {
        User user = authService.getCurrentUser();

        // Validate amount
        if (request.getAmount() == null || request.getAmount() <= 0) {
            throw new BadRequestException("Amount must be positive");
        }

        // Validate date format and not future
        if (request.getDate() == null || request.getDate().isBlank()) {
            throw new BadRequestException("Date is required");
        }
        try {
            LocalDate date = LocalDate.parse(request.getDate());
            if (date.isAfter(LocalDate.now())) {
                throw new BadRequestException("Date cannot be in the future");
            }
        } catch (DateTimeParseException e) {
            throw new BadRequestException("Invalid date format. Use YYYY-MM-DD");
        }

        // Validate category
        if (request.getCategory() == null || request.getCategory().isBlank()) {
            throw new BadRequestException("Category is required");
        }

        // Find category - check user's custom categories and default categories
        Category category = categoryRepository.findByNameAndUser(request.getCategory(), user)
                .orElseGet(() -> categoryRepository.findByNameAndUserIsNull(request.getCategory())
                        .orElseThrow(() -> new BadRequestException("Invalid category: " + request.getCategory()))
                );

        Transaction transaction = Transaction.builder()
                .amount(request.getAmount())
                .date(request.getDate())
                .description(request.getDescription())
                .category(category.getName())
                .type(category.getType())
                .user(user)
                .build();

        transaction = transactionRepository.save(transaction);

        return buildTransactionResponse(transaction);
    }

    /**
     * Get all transactions for the current user, optionally filtered.
     */
    public Map<String, Object> getAll(String startDate, String endDate, String categoryName, Long categoryId) {
        User user = authService.getCurrentUser();

        List<Transaction> transactions;

        if (startDate != null && endDate != null) {
            transactions = transactionRepository.findByUserAndDateBetweenOrderByDateDescIdDesc(
                    user, startDate, endDate);
        } else {
            transactions = transactionRepository.findByUserOrderByDateDescIdDesc(user);
        }

        // Filter by categoryName if provided
        if (categoryName != null && !categoryName.isBlank()) {
            transactions = transactions.stream()
                    .filter(t -> t.getCategory().equalsIgnoreCase(categoryName))
                    .collect(Collectors.toList());
        }

        // Filter by categoryId if provided
        if (categoryId != null) {
            Category cat = categoryRepository.findById(categoryId).orElse(null);
            if (cat != null) {
                String catName = cat.getName();
                transactions = transactions.stream()
                        .filter(t -> t.getCategory().equals(catName))
                        .collect(Collectors.toList());
            }
        }

        List<Map<String, Object>> transactionList = transactions.stream()
                .map(this::buildTransactionResponse)
                .collect(Collectors.toList());

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("transactions", transactionList);
        return response;
    }

    /**
     * Update a transaction.
     */
    public Map<String, Object> update(Long id, UpdateTransactionRequest request) {
        User user = authService.getCurrentUser();

        Transaction transaction = transactionRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found"));

        if (request.getAmount() != null) {
            if (request.getAmount() <= 0) {
                throw new BadRequestException("Amount must be positive");
            }
            transaction.setAmount(request.getAmount());
        }

        if (request.getDescription() != null) {
            transaction.setDescription(request.getDescription());
        }

        if (request.getCategory() != null) {
            // Validate category
            Category category = categoryRepository.findByNameAndUser(request.getCategory(), user)
                    .orElseGet(() -> categoryRepository.findByNameAndUserIsNull(request.getCategory())
                            .orElseThrow(() -> new BadRequestException("Invalid category: " + request.getCategory()))
                    );
            transaction.setCategory(category.getName());
            transaction.setType(category.getType());
        }

        transaction = transactionRepository.save(transaction);

        return buildTransactionResponse(transaction);
    }

    /**
     * Delete a transaction.
     */
    public Map<String, Object> delete(Long id) {
        User user = authService.getCurrentUser();

        Transaction transaction = transactionRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new ResourceNotFoundException("Transaction not found"));

        transactionRepository.delete(transaction);

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("message", "Transaction deleted successfully");
        return response;
    }

    private Map<String, Object> buildTransactionResponse(Transaction t) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("id", t.getId());
        map.put("amount", t.getAmount());
        map.put("date", t.getDate());
        map.put("category", t.getCategory());
        map.put("description", t.getDescription());
        map.put("type", t.getType());
        return map;
    }
}