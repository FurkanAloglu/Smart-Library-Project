package com.furkan.smart_library_backend.book;

import com.furkan.smart_library_backend.author.Author;
import com.furkan.smart_library_backend.author.AuthorRepository;
import com.furkan.smart_library_backend.book.dto.BookRequest;
import com.furkan.smart_library_backend.book.dto.BookResponse;
import com.furkan.smart_library_backend.category.Category;
import com.furkan.smart_library_backend.category.CategoryRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class BookService {

    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;
    private final CategoryRepository categoryRepository;

    public List<BookResponse> getAllBooks() {
        try {
            return bookRepository.findAllByDeletedFalse().stream()
                    .map(BookResponse::fromEntity)
                    .toList();
        } catch (Exception e) {
            log.error("Error in BookService.getAllBooks", e);
            throw new RuntimeException("Failed to get books", e);
        }
    }

    @Transactional(readOnly = true)
    public BookResponse getBookById(UUID id) {
        try {
            Book book = bookRepository.findByIdAndDeletedFalse(id)
                    .orElseThrow(() -> new EntityNotFoundException("Book not found with id: " + id));
            return BookResponse.fromEntity(book);
        } catch (Exception e) {
            log.error("Error in BookService.getBookById id={}", id, e);
            throw new RuntimeException("Failed to get book by id", e);
        }
    }

    @Transactional
    public BookResponse createBook(BookRequest request) {
        try {
            // ISBN Kontrolü
            if (bookRepository.existsByIsbnAndDeletedFalse(request.isbn())) {
                throw new IllegalArgumentException("Book with this ISBN already exists");
            }

            // İlişkileri Getir (Senkron çalışır, olması gereken budur)
            Set<Author> authors = fetchAuthors(request.authorIds());
            Set<Category> categories = fetchCategories(request.categoryIds());

            try {
                // Builder ile nesne oluşturulurken .id() ATAMASI YAPMA!
                Book book = Book.builder()
                        // .id(UUID.randomUUID())  <-- BU SATIRI SİL! JPA KENDİSİ VERECEK.
                        .title(request.title())
                        .description(request.description())
                        .isbn(request.isbn())
                        .stock(request.stock())
                        .imageUrl(request.imageUrl())
                        .authors(authors)
                        .categories(categories)
                        .deleted(false)
                        .build();

                // save metodu Transaction bitince commit eder.
                return BookResponse.fromEntity(bookRepository.save(book));

            } catch (DataAccessException e) {
                log.error("Database error in BookService.createBook request={}", request, e);
                throw new RuntimeException("Veritabanı işlemi sırasında teknik bir hata: " + e.getMessage(), e);
            }
        } catch (Exception e) {
            log.error("Error in BookService.createBook request={}", request, e);
            throw new RuntimeException("Failed to create book", e);
        }
    }

    @Transactional
    public BookResponse updateBook(UUID id, BookRequest request) {
        try {
            Book book = bookRepository.findByIdAndDeletedFalse(id)
                    .orElseThrow(() -> new EntityNotFoundException("Book not found"));

            if (!book.getIsbn().equals(request.isbn()) && bookRepository.existsByIsbnAndDeletedFalse(request.isbn())) {
                throw new IllegalArgumentException("ISBN already in use");
            }

            Set<Author> authors = fetchAuthors(request.authorIds());
            Set<Category> categories = fetchCategories(request.categoryIds());

            book.setTitle(request.title());
            book.setDescription(request.description());
            book.setIsbn(request.isbn());
            book.setStock(request.stock());
            book.setImageUrl(request.imageUrl());
            book.setAuthors(authors);
            book.setCategories(categories);

            return BookResponse.fromEntity(bookRepository.save(book));
        } catch (Exception e) {
            log.error("Error in BookService.updateBook id={}", id, e);
            throw new RuntimeException("Failed to update book", e);
        }
    }

    @Transactional
    public void deleteBook(UUID id) {
        try {
            Book book = bookRepository.findByIdAndDeletedFalse(id)
                    .orElseThrow(() -> new EntityNotFoundException("Book not found"));
            book.setDeleted(true);
            bookRepository.save(book);
        } catch (Exception e) {
            log.error("Error in BookService.deleteBook id={}", id, e);
            throw new RuntimeException("Failed to delete book", e);
        }
    }

    private Set<Author> fetchAuthors(Set<UUID> ids) {
        try {
            List<Author> found = authorRepository.findAllById(ids);
            if (found.size() != ids.size()) {
                throw new EntityNotFoundException("Some authors were not found");
            }
            return new HashSet<>(found);
        } catch (Exception e) {
            log.error("Error in BookService.fetchAuthors ids={}", ids, e);
            throw new RuntimeException("Failed to fetch authors", e);
        }
    }

    private Set<Category> fetchCategories(Set<UUID> ids) {
        try {
            List<Category> found = categoryRepository.findAllById(ids);
            if (found.size() != ids.size()) {
                throw new EntityNotFoundException("Some categories were not found");
            }
            return new HashSet<>(found);
        } catch (Exception e) {
            log.error("Error in BookService.fetchCategories ids={}", ids, e);
            throw new RuntimeException("Failed to fetch categories", e);
        }
    }
}