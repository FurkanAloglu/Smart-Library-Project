package com.furkan.smart_library_backend.author.dto;

import com.furkan.smart_library_backend.author.Author;
import java.util.UUID;

public record AuthorResponse(
        UUID id,
        String name
) {
    public static AuthorResponse fromEntity(Author author) {
        return new AuthorResponse(author.getId(), author.getName());
    }
}