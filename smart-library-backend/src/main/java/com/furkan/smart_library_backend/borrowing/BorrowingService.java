package com.furkan.smart_library_backend.borrowing;

import com.furkan.smart_library_backend.book.Book;
import com.furkan.smart_library_backend.book.BookRepository;
import com.furkan.smart_library_backend.borrowing.dto.BorrowingRequest;
import com.furkan.smart_library_backend.borrowing.dto.BorrowingResponse;
import com.furkan.smart_library_backend.mail.MailService;
import com.furkan.smart_library_backend.penalty.Penalty;
import com.furkan.smart_library_backend.penalty.PenaltyRepository;
import com.furkan.smart_library_backend.user.User;
import com.furkan.smart_library_backend.user.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class BorrowingService {

    private final BorrowingRepository borrowingRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;
    private final MailService mailService;
    private final PenaltyRepository penaltyRepository;

    public List<BorrowingResponse> getMyBorrowings(String email) {
        User user = userRepository.findByEmailAndDeletedFalse(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        return borrowingRepository.findAllByUserIdOrderByBorrowDateDesc(user.getId())
                .stream()
                .map(BorrowingResponse::fromEntity)
                .toList();
    }

    public List<BorrowingResponse> getAllBorrowings() {
        return borrowingRepository.findAll()
                .stream()
                .map(BorrowingResponse::fromEntity)
                .toList();
    }

    // ================= BORROW =================
    @Transactional
    public BorrowingResponse borrowBook(BorrowingRequest request, String userEmail) {

        User user = userRepository.findByEmailAndDeletedFalse(userEmail)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        if (!penaltyRepository.findAllByUserId(user.getId()).isEmpty()) {
            throw new IllegalStateException("Ödenmemiş cezalarınız bulunmaktadır.");
        }

        Book book = bookRepository.findByIdAndDeletedFalse(request.bookId())
                .orElseThrow(() -> new EntityNotFoundException("Book not found"));

        if (book.getStock() <= 0) {
            throw new IllegalStateException("Book is out of stock");
        }

        boolean alreadyBorrowed =
                borrowingRepository.existsByUserIdAndBookIdAndReturnDateIsNull(
                        user.getId(), book.getId()
                );

        if (alreadyBorrowed) {
            throw new IllegalStateException("Bu kitabı zaten ödünç almışsınız");
        }

        Borrowing borrowing = Borrowing.builder()
                .user(user)
                .book(book)
                .borrowDate(LocalDateTime.now())
                .dueDate(LocalDateTime.now().plusDays(14)) // PROD
                // .dueDate(LocalDateTime.now().plusMinutes(1)) // TEST
                .build();

        Borrowing saved = borrowingRepository.save(borrowing);
        return BorrowingResponse.fromEntity(saved);
    }

    // ================= RETURN =================
    @Transactional
    public void returnBook(UUID borrowingId, String userEmail) {

        Borrowing borrowing = borrowingRepository.findById(borrowingId)
                .orElseThrow(() -> new EntityNotFoundException("Borrowing not found"));

        if (!borrowing.getUser().getEmail().equals(userEmail)) {
            throw new IllegalStateException("Sadece kendi kitabını iade edebilirsin");
        }

        if (borrowing.getReturnDate() != null) {
            throw new IllegalStateException("Kitap zaten iade edilmiş");
        }

        borrowing.setReturnDate(LocalDateTime.now());

        borrowingRepository.save(borrowing);
        borrowingRepository.flush();

        log.info(
                "İADE | BorrowingId={} | Due={} | Return={} | Late={}",
                borrowing.getId(),
                borrowing.getDueDate(),
                borrowing.getReturnDate(),
                borrowing.getReturnDate().isAfter(borrowing.getDueDate())
        );

        if (borrowing.getReturnDate().isAfter(borrowing.getDueDate())) {
            mailService.sendLateReturnNotification(
                    borrowing.getUser().getEmail(),
                    borrowing.getBook().getTitle(),
                    borrowing.getUser().getFullName()
            );
        }
    }
}
