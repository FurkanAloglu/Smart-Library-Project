package com.furkan.smart_library_backend.book.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.Set;
import java.util.UUID;

public record BookRequest(
        @NotBlank(message = "Title cannot be empty")
        String title,

        String description,

        @NotBlank(message = "ISBN cannot be empty")
        String isbn,

        @Min(value = 0, message = "Stock cannot be negative")
        int stock,

        String imageUrl,

        @NotEmpty(message = "At least one author is required")
        Set<UUID> authorIds,

        @NotEmpty(message = "At least one category is required")
        Set<UUID> categoryIds
) {}