package com.furkan.smart_library_backend.category.dto;

import com.furkan.smart_library_backend.category.Category;
import java.util.UUID;

public record CategoryResponse(
        UUID id,
        String name
) {
    public static CategoryResponse fromEntity(Category category) {
        return new CategoryResponse(category.getId(), category.getName());
    }
}