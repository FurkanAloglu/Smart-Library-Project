package com.furkan.smart_library_backend.borrowing;

import com.furkan.smart_library_backend.book.Book;
import com.furkan.smart_library_backend.book.BookRepository;
import com.furkan.smart_library_backend.borrowing.dto.BorrowingRequest;
import com.furkan.smart_library_backend.borrowing.dto.BorrowingResponse;
import com.furkan.smart_library_backend.mail.MailService;
import com.furkan.smart_library_backend.user.User;
import com.furkan.smart_library_backend.user.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BorrowingService {

    private final BorrowingRepository borrowingRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;
    private final MailService mailService;

    private static final BigDecimal DAILY_FEE = BigDecimal.valueOf(5.0);

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

    @Transactional
    public BorrowingResponse borrowBook(BorrowingRequest request, String userEmail) {
        User user = userRepository.findByEmailAndDeletedFalse(userEmail)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        Book book = bookRepository.findByIdAndDeletedFalse(request.bookId())
                .orElseThrow(() -> new EntityNotFoundException("Book not found"));

        if (book.getStock() <= 0) {
            throw new IllegalStateException("Book is out of stock");
        }

        boolean alreadyHas = borrowingRepository.existsByUserIdAndBookIdAndReturnDateIsNull(user.getId(), book.getId());
        if (alreadyHas) {
            throw new IllegalStateException("You already have this book borrowed");
        }

        Borrowing borrowing = Borrowing.builder()
                .user(user)
                .book(book)
                .borrowDate(LocalDate.now())
                .dueDate(LocalDate.now().plusDays(15))
                .build();

        Borrowing saved = borrowingRepository.save(borrowing);
        return BorrowingResponse.fromEntity(saved);
    }

    @Transactional
    public void returnBook(UUID borrowingId, String userEmail) {
        Borrowing borrowing = borrowingRepository.findById(borrowingId)
                .orElseThrow(() -> new EntityNotFoundException("Borrowing record not found"));

        if (borrowing.getReturnDate() != null) {
            throw new IllegalStateException("Book already returned");
        }

        borrowing.setReturnDate(LocalDate.now());
        borrowingRepository.save(borrowing);

        borrowingRepository.flush();

        borrowingRepository.callCalculatePenalty(borrowing.getId(), DAILY_FEE);

        if (borrowing.getReturnDate().isAfter(borrowing.getDueDate())) {
            mailService.sendLateReturnNotification(
                    borrowing.getUser().getEmail(),
                    borrowing.getBook().getTitle(),
                    borrowing.getUser().getFullName()
            );
        }
    }
}