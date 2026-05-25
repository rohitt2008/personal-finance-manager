package personal_finance_manager.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import personal_finance_manager.dto.CreateCategoryRequest;
import personal_finance_manager.entity.Category;
import personal_finance_manager.entity.User;
import personal_finance_manager.exception.BadRequestException;
import personal_finance_manager.exception.ConflictException;
import personal_finance_manager.exception.ForbiddenException;
import personal_finance_manager.exception.ResourceNotFoundException;
import personal_finance_manager.repository.CategoryRepository;
import personal_finance_manager.repository.TransactionRepository;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final TransactionRepository transactionRepository;
    private final AuthService authService;

    /**
     * Create a custom category for the current user.
     */
    public Map<String, Object> create(CreateCategoryRequest request) {
        User user = authService.getCurrentUser();

        // Validate type
        if (request.getType() == null || request.getType().isBlank()) {
            throw new BadRequestException("Category type is required");
        }
        String type = request.getType().toUpperCase();
        if (!type.equals("INCOME") && !type.equals("EXPENSE")) {
            throw new BadRequestException("Category type must be INCOME or EXPENSE");
        }

        // Validate name
        if (request.getName() == null || request.getName().isBlank()) {
            throw new BadRequestException("Category name is required");
        }

        // Check for duplicate among user's custom categories
        if (categoryRepository.existsByNameAndUser(request.getName(), user)) {
            throw new ConflictException("Category already exists");
        }

        // Also check against default categories
        if (categoryRepository.existsByNameAndUserIsNull(request.getName())) {
            throw new ConflictException("Category already exists");
        }

        Category category = Category.builder()
                .name(request.getName())
                .type(type)
                .isCustom(true)
                .user(user)
                .build();

        categoryRepository.save(category);

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("name", category.getName());
        response.put("type", category.getType());
        response.put("custom", true);
        response.put("isCustom", true);
        return response;
    }

    /**
     * Get all categories (default + user's custom).
     */
    public Map<String, Object> getAll() {
        User user = authService.getCurrentUser();

        List<Category> categories = categoryRepository.findByUserOrUserIsNull(user);

        List<Map<String, Object>> categoryList = categories.stream()
                .map(c -> {
                    Map<String, Object> map = new LinkedHashMap<>();
                    map.put("name", c.getName());
                    map.put("type", c.getType());
                    map.put("custom", c.getIsCustom());
                    map.put("isCustom", c.getIsCustom());
                    return map;
                })
                .collect(Collectors.toList());

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("categories", categoryList);
        return response;
    }

    /**
     * Delete a custom category by name.
     */
    public Map<String, Object> delete(String name) {
        User user = authService.getCurrentUser();

        // First check if it's a default category
        Optional<Category> defaultCat = categoryRepository.findByNameAndUserIsNull(name);
        if (defaultCat.isPresent()) {
            throw new ForbiddenException("Cannot delete default category");
        }

        // Find user's custom category
        Category category = categoryRepository.findByNameAndUser(name, user)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));

        if (!category.getIsCustom()) {
            throw new ForbiddenException("Cannot delete default category");
        }

        // Check if category is in use by transactions
        if (transactionRepository.existsByCategoryAndUser(name, user)) {
            throw new BadRequestException("Cannot delete category that is in use by transactions");
        }

        categoryRepository.delete(category);

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("message", "Category deleted successfully");
        return response;
    }
}