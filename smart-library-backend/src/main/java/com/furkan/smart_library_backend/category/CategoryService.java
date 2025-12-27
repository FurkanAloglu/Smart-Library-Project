package com.furkan.smart_library_backend.category;

import com.furkan.smart_library_backend.category.dto.CategoryRequest;
import com.furkan.smart_library_backend.category.dto.CategoryResponse;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public List<CategoryResponse> getAllCategories() {
        return categoryRepository.findAllByDeletedFalse().stream()
                .map(CategoryResponse::fromEntity)
                .toList();
    }

    @Transactional
    public CategoryResponse createCategory(CategoryRequest request) {
        String cleanName = request.name().trim();

        if (categoryRepository.existsByNameIgnoreCaseAndDeletedFalse(cleanName)) {
            throw new IllegalArgumentException("Bu kategori zaten mevcut!");
        }

        Category category = Category.builder()
                .name(cleanName)
                .deleted(false)
                .build();
        return CategoryResponse.fromEntity(categoryRepository.save(category));
    }

    @Transactional
    public CategoryResponse updateCategory(UUID id, CategoryRequest request) {
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
    }

    @Transactional
    public void deleteCategory(UUID id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Category not found"));
        category.setDeleted(true); // Soft Delete
        categoryRepository.save(category);
    }
}