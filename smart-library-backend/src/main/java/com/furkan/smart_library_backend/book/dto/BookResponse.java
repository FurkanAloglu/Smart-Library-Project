package com.furkan.smart_library_backend.book.dto;

import com.furkan.smart_library_backend.author.dto.AuthorResponse;
import com.furkan.smart_library_backend.book.Book;
import com.furkan.smart_library_backend.category.dto.CategoryResponse;

import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public record BookResponse(
        UUID id,
        String title,
        String description,
        String isbn,
        int stock,
        String imageUrl,
        Set<AuthorResponse> authors,
        Set<CategoryResponse> categories
) {
    public static BookResponse fromEntity(Book book) {
        return new BookResponse(
                book.getId(),
                book.getTitle(),
                book.getDescription(),
                book.getIsbn(),
                book.getStock(),
                book.getImageUrl(),

                book.getAuthors().stream().map(AuthorResponse::fromEntity).collect(Collectors.toSet()),
                book.getCategories().stream().map(CategoryResponse::fromEntity).collect(Collectors.toSet())
        );
    }
}