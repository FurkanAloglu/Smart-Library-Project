package com.furkan.smart_library_backend.borrowing.dto;

import com.furkan.smart_library_backend.borrowing.Borrowing;
import java.time.LocalDateTime;
import java.util.UUID;

public record BorrowingResponse(
        UUID id,
        String bookTitle,
        String bookIsbn,
        String bookImageUrl,
        String userName,
        LocalDateTime borrowDate,
        LocalDateTime dueDate,
        LocalDateTime returnDate,
        boolean isReturned
) {
    public static BorrowingResponse fromEntity(Borrowing borrowing) {
        return new BorrowingResponse(
                borrowing.getId(),
                borrowing.getBook().getTitle(),
                borrowing.getBook().getIsbn(),
                borrowing.getBook().getImageUrl(),
                borrowing.getUser().getFullName(),
                borrowing.getBorrowDate(),
                borrowing.getDueDate(),
                borrowing.getReturnDate(),
                borrowing.getReturnDate() != null
        );
    }
}