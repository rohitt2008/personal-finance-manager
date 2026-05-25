package personal_finance_manager.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import personal_finance_manager.dto.CreateCategoryRequest;
import personal_finance_manager.service.CategoryService;

import java.util.Map;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    /**
     * Create a custom category.
     * POST /api/categories
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> create(@Valid @RequestBody CreateCategoryRequest request) {
        Map<String, Object> response = categoryService.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Get all categories (default + custom).
     * GET /api/categories
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAll() {
        Map<String, Object> response = categoryService.getAll();
        return ResponseEntity.ok(response);
    }

    /**
     * Delete a custom category by name.
     * DELETE /api/categories/{name}
     */
    @DeleteMapping("/{name}")
    public ResponseEntity<Map<String, Object>> delete(@PathVariable String name) {
        Map<String, Object> response = categoryService.delete(name);
        return ResponseEntity.ok(response);
    }
}