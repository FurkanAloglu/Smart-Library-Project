package com.furkan.smart_library_backend.book;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface BookRepository extends JpaRepository<Book, UUID> {

    @EntityGraph(attributePaths = {"authors", "categories"})
    List<Book> findAllByDeletedFalse();

    @EntityGraph(attributePaths = {"authors", "categories"})
    Optional<Book> findByIdAndDeletedFalse(UUID id);

    boolean existsByIsbnAndDeletedFalse(String isbn);

    @Override
    @EntityGraph(attributePaths = {"authors", "categories"})
    List<Book> findAll();
}