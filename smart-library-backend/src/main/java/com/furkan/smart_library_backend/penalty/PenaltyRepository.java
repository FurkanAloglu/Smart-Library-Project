package com.furkan.smart_library_backend.penalty;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PenaltyRepository extends JpaRepository<Penalty, UUID> {

    @Query("SELECT p FROM Penalty p WHERE p.borrowing.user.id = :userId ORDER BY p.createdAt DESC")
    List<Penalty> findAllByUserId(@Param("userId") UUID userId);

    @Override
    @EntityGraph(attributePaths = {"borrowing", "borrowing.user", "borrowing.book"})
    List<Penalty> findAll();
}