package com.project.techstore.services.category;

import java.util.List;

import com.project.techstore.models.Category;

public interface ICategoryService {
    public List<Category> getAllCategories();

    public Category getCategoryById(Long id);

    public Category createCategory(String name);

    public Category updateCategory(Long id, String name);

    public void deleteCategory(Long id);

    public boolean existsById(Long id);
}
