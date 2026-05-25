package personal_finance_manager.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import personal_finance_manager.dto.CreateCategoryRequest;
import personal_finance_manager.entity.Category;
import personal_finance_manager.repository.CategoryRepository;

import java.util.List;

@Service
@RequiredArgsConstructor

public class CategoryService {

    private final
    CategoryRepository categoryRepository;


    public String create(
            CreateCategoryRequest request
    ) {


        if (

                categoryRepository
                        .findByName(
                                request.getName()
                        )

                        .isPresent()

        ) {

            throw new RuntimeException(
                    "Category already exists"
            );

        }


        Category category =
                Category.builder()

                        .name(
                                request.getName()
                        )

                        .type(
                                request.getType()
                        )

                        .isCustom(
                                true
                        )

                        .build();


        categoryRepository.save(
                category
        );

        return "Category created";

    }


    public List<Category> getAll() {

        return categoryRepository.findAll();

    }

}