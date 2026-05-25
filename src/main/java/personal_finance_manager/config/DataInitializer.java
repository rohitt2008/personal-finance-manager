package personal_finance_manager.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import personal_finance_manager.entity.Category;
import personal_finance_manager.repository.CategoryRepository;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final CategoryRepository categoryRepository;

    @Override
    public void run(String... args) {
        // Seed default categories (only if they don't exist)
        createDefaultCategoryIfNotExists("Salary", "INCOME");
        createDefaultCategoryIfNotExists("Food", "EXPENSE");
        createDefaultCategoryIfNotExists("Rent", "EXPENSE");
        createDefaultCategoryIfNotExists("Transportation", "EXPENSE");
        createDefaultCategoryIfNotExists("Entertainment", "EXPENSE");
        createDefaultCategoryIfNotExists("Healthcare", "EXPENSE");
        createDefaultCategoryIfNotExists("Utilities", "EXPENSE");
    }

    private void createDefaultCategoryIfNotExists(String name, String type) {
        if (categoryRepository.findByNameAndUserIsNull(name).isEmpty()) {
            Category category = Category.builder()
                    .name(name)
                    .type(type)
                    .isCustom(false)
                    .user(null)
                    .build();
            categoryRepository.save(category);
        }
    }
}
