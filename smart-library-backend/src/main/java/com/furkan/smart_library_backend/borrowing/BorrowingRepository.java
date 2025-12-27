package com.furkan.smart_library_backend.borrowing;

import com.furkan.smart_library_backend.book.Book;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface BorrowingRepository extends JpaRepository<Borrowing, UUID> {

    List<Borrowing> findAllByUserIdOrderByBorrowDateDesc(UUID userId);

    @Query("SELECT b FROM Borrowing b WHERE b.book.id = :bookId AND b.returnDate IS NULL")
    Optional<Borrowing> findActiveBorrowingByBookId(UUID bookId);

    boolean existsByUserIdAndBookIdAndReturnDateIsNull(UUID userId, UUID bookId);

    // calculate_penalty(borrowing_id, daily_fee)
    @Query(value = "SELECT calculate_penalty(:borrowingId, :dailyFee)", nativeQuery = true)
    BigDecimal callCalculatePenalty(@Param("borrowingId") UUID borrowingId, @Param("dailyFee") BigDecimal dailyFee);
}