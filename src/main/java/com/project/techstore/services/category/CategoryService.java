package com.project.techstore.services.category;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.project.techstore.models.Category;
import com.project.techstore.repositories.CategoryRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CategoryService implements ICategoryService {

    private final CategoryRepository categoryRepository;

    public List<Category> getAllCategories() {
        return categoryRepository.findAll();
    }

    public Category getCategoryById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found with id: " + id));
        return category;
    }

    @Transactional
    public Category createCategory(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new RuntimeException("Category name cannot be empty.");
        }
        if (categoryRepository.existsByName(name)) {
            throw new RuntimeException("Category with name '" + name + "' already exists.");
        }
        Category category = new Category();
        category.setName(name);

        Category savedCategory = categoryRepository.save(category);
        return savedCategory;
    }

    @Transactional
    public Category updateCategory(Long id, String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new RuntimeException("Category name cannot be empty.");
        }
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Category not found with id: " + id));

        category.setName(name);

        Category updatedCategory = categoryRepository.save(category);
        return updatedCategory;
    }

    @Transactional
    public void deleteCategory(Long id) {
        if (!categoryRepository.existsById(id)) {
            throw new RuntimeException("Category not found with id: " + id);
        }
        categoryRepository.deleteById(id);
    }

    public boolean existsById(Long id) {
        return categoryRepository.existsById(id);
    }
}
