package com.furkan.smart_library_backend.author.dto;

import jakarta.validation.constraints.NotBlank;

public record AuthorRequest(
        @NotBlank(message = "Author name cannot be empty")
        String name
) {}