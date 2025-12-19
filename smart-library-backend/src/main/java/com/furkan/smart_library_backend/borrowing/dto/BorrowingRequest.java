package com.furkan.smart_library_backend.borrowing.dto;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record BorrowingRequest(
        @NotNull(message = "Book ID is required")
        UUID bookId
) {}