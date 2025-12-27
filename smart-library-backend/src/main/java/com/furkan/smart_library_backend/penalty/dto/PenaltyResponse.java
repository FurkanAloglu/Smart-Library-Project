package com.furkan.smart_library_backend.penalty.dto;

import com.furkan.smart_library_backend.penalty.Penalty;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record PenaltyResponse(
        UUID id,
        BigDecimal amount,
        String bookTitle,
        String userName,
        LocalDateTime returnDate,
        LocalDateTime dueDate,
        boolean isPaid // EKLENDİ
) {
    public static PenaltyResponse fromEntity(Penalty penalty) {
        return new PenaltyResponse(
                penalty.getId(),
                penalty.getAmount(),
                penalty.getBorrowing().getBook().getTitle(),
                penalty.getBorrowing().getUser().getFullName(),
                penalty.getBorrowing().getReturnDate(),
                penalty.getBorrowing().getDueDate(),
                penalty.isPaid() // EKLENDİ
        );
    }
}