package personal_finance_manager.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import personal_finance_manager.dto.CreateCategoryRequest;
import personal_finance_manager.entity.Category;
import personal_finance_manager.service.CategoryService;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor

public class CategoryController {

    private final CategoryService categoryService;


    @PostMapping
    public String create(

            @RequestBody
            CreateCategoryRequest request

    ) {

        return categoryService.create(
                request
        );

    }


    @GetMapping
    public List<Category> getAll() {

        return categoryService.getAll();

    }

}