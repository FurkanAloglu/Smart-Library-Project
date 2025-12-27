package com.furkan.smart_library_backend.category;

import com.furkan.smart_library_backend.category.dto.CategoryRequest;
import com.furkan.smart_library_backend.category.dto.CategoryResponse;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public List<CategoryResponse> getAllCategories() {
        try {
            return categoryRepository.findAllByDeletedFalse().stream()
                    .map(CategoryResponse::fromEntity)
                    .toList();
        } catch (Exception e) {
            log.error("Error in CategoryService.getAllCategories", e);
            throw new RuntimeException("Failed to get categories", e);
        }
    }

    @Transactional
    public CategoryResponse createCategory(CategoryRequest request) {
        try {
            String cleanName = request.name().trim();

            if (categoryRepository.existsByNameIgnoreCaseAndDeletedFalse(cleanName)) {
                throw new IllegalArgumentException("Bu kategori zaten mevcut!");
            }

            Category category = Category.builder()
                    .name(cleanName)
                    .deleted(false)
                    .build();
            return CategoryResponse.fromEntity(categoryRepository.save(category));
        } catch (Exception e) {
            log.error("Error in CategoryService.createCategory request={}", request, e);
            throw new RuntimeException("Failed to create category", e);
        }
    }

    @Transactional
    public CategoryResponse updateCategory(UUID id, CategoryRequest request) {
        try {
            Category category = categoryRepository.findById(id)
                    .filter(c -> !c.isDeleted())
                    .orElseThrow(() -> new EntityNotFoundException("Category not found"));

            String newName = request.name().trim();

            if (!category.getName().equalsIgnoreCase(newName) &&
                    categoryRepository.existsByNameIgnoreCaseAndDeletedFalse(newName)) {
                throw new IllegalArgumentException("Bu kategori adı zaten sistemde mevcut!");
            }

            category.setName(newName);
            return CategoryResponse.fromEntity(categoryRepository.save(category));
        } catch (Exception e) {
            log.error("Error in CategoryService.updateCategory id={}", id, e);
            throw new RuntimeException("Failed to update category", e);
        }
    }

    @Transactional
    public void deleteCategory(UUID id) {
        try {
            Category category = categoryRepository.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException("Category not found"));
            category.setDeleted(true); // Soft Delete
            categoryRepository.save(category);
        } catch (Exception e) {
            log.error("Error in CategoryService.deleteCategory id={}", id, e);
            throw new RuntimeException("Failed to delete category", e);
        }
    }
}